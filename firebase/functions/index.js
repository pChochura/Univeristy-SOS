const functions = require('firebase-functions');
const admin = require('firebase-admin');
const serviceAccount = require('./config.json');
const { firestore } = require('firebase-admin');
const { OAuth } = require('oauth');
const fetch = require('node-fetch');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: 'https://usoschedule-221315.firebaseio.com',
});

exports.postNotification = functions.https.onRequest(async (req, res) => {
  if (req.query['hub.mode'] === 'subscribe') {
    // PubSubHubbub protocol
    res.contentType('text/plain').status(200).send(req.query['hub.challenge']);

    return;
  }

  if (!req.body.entry) {
    res.sendStatus(400);

    return;
  }

  if (!req.body.event_type) {
    res.sendStatus(400);

    return;
  }

  const eventType = req.body.event_type;

  await Promise.all(
    req.body.entry.map(async (entry) => {
      if (!entry.operation) {
        return;
      }

      const operation = entry.operation;
      let tokensSnapshot = admin.firestore().collection('fcmTokens');

      if (
        entry.related_user_ids &&
        (entry.related_user_ids.length > 1 || entry.related_user_ids[0] !== '*')
      ) {
        tokensSnapshot = tokensSnapshot.where(
          firestore.FieldPath.documentId(),
          'in',
          entry.related_user_ids
        );
      }

      const tokens = (await tokensSnapshot.get()).docs.map((doc) =>
        doc.get('token')
      );

      return admin.messaging().sendToDevice(tokens, {
        data: { eventType, operation },
      });
    })
  );

  res.sendStatus(200);
});

const getArrayDiff = (arr1, arr2) => {
  arr1.forEach((e) => {
    const index = arr2.indexOf(e);
    if (index !== -1) {
      arr2.splice(index, 1);
    }
  });

  return arr2;
};

exports.checkNotifications = functions.pubsub
  .schedule('every 30 minutes from 6:00 to 22:00')
  .onRun(async () => {
    const users = (await admin.firestore().collection('fcmTokens').get()).docs
      .filter((doc) => doc.get('accessToken') && doc.get('serviceUrl'))
      .map((doc) => ({
        id: doc.id,
        accessToken: doc.get('accessToken'),
        serviceUrl: doc.get('serviceUrl'),
        surveysIds: doc.get('surveysIds'),
        articlesIds: doc.get('articlesIds'),
        token: doc.get('token'),
      }));

    users.forEach(async (user) => {
      const university = (
        await admin
          .firestore()
          .collection('universities')
          .where('serviceUrl', '==', user.serviceUrl)
          .get()
      ).docs.map((doc) => ({
        key: doc.get('consumerKey'),
        secret: doc.get('consumerSecret'),
      }))[0];

      const oauth = new OAuth(
        '',
        '',
        university.key,
        university.secret,
        '1.0',
        null,
        'HMAC-SHA1'
      );

      const articles = (
        await (
          await fetch(
            oauth.signUrl(
              `${user.serviceUrl}/news/search?num=20`,
              user.accessToken.token,
              user.accessToken.secret
            )
          )
        ).json()
      ).items.map((article) => article.article.id);

      const surveys = (
        await (
          await fetch(
            oauth.signUrl(
              `${user.serviceUrl}/surveys/surveys_to_fill`,
              user.accessToken.token,
              user.accessToken.secret
            )
          )
        ).json()
      ).map((survey) => survey.id);

      if (getArrayDiff(user.articlesIds, articles).length > 0) {
        await admin
          .firestore()
          .collection('fcmTokens')
          .doc(user.id)
          .set(
            { articlesIds: [...articles, ...user.articlesIds] },
            { merge: true }
          );

        admin.messaging().sendToDevice(user.token, {
          data: { eventType: 'news/articles' },
        });
      }

      if (getArrayDiff(user.surveysIds, surveys).length > 0) {
        await admin
          .firestore()
          .collection('fcmTokens')
          .doc(user.id)
          .set(
            { surveysIds: [...surveys, ...user.surveysIds] },
            { merge: true }
          );

        admin.messaging().sendToDevice(user.token, {
          data: { eventType: 'surveys/surveys' },
        });
      }
    });
  });
