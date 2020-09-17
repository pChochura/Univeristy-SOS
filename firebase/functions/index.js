const functions = require('firebase-functions');
const admin = require('firebase-admin');
const serviceAccount = require('./config.json');
const { firestore } = require('firebase-admin');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: 'https://usoschedule-221315.firebaseio.com',
});

exports.postNotification = functions.https.onRequest(async (req, res) => {
  if (req.query.hub && req.query.hub.mode === 'subscribe') {
    // PubSubHubbub protocol
    res.send(req.query.hub.challenge);

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
      const tokensSnapshot = admin.firestore().collection('fcmTokens');

      if (
        entry.related_user_ids &&
        (entry.related_user_ids.length > 1 || entry.related_user_ids[0] !== '*')
      ) {
        tokensSnapshot.where(
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
