package com.ham.activitymonitorapp.exceptions

class HrNotFoundException(hrId: Int) : Exception("Hr with hrId $hrId not found")
