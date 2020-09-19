package com.pointlessapps.mobileusos.exceptions

class ExceptionFragmentContainerEmpty(message: String) : Exception(message)

class ExceptionNullKeyOrSecret(message: String) : Exception(message)

class ExceptionNotInitialized(message: String) : Exception(message)

class ExceptionHttpUnsuccessful(message: String, val code: Int) : Exception(message)
