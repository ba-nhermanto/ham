package com.ham.activitymonitorapp.exceptions

class HrNotFoundException(hrId: Long) : Exception("Hr with hrId $hrId not found")
