package com.ham.activitymonitorapp.exception

class HrNotFoundException(hrId: Int) : Exception("Hr with hrId $hrId not found")
