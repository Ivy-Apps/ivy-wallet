package com.ivy.core.features

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IvyFeatures @Inject constructor() : Features {
    override val homeV2 = BoolFeature("home_v2")
}