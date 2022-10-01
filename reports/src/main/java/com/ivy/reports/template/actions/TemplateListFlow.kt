package com.ivy.reports.template.actions

import com.ivy.core.action.FlowAction
import com.ivy.reports.template.data.Template
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TemplateListFlow @Inject constructor() :
    FlowAction<Unit, List<Template>>() {
    override fun Unit.createFlow(): Flow<List<Template>> = filterList()


    private fun filterList(): Flow<List<Template>> =
        flowOf(emptyList<Template>()).flowOn(Dispatchers.IO)
}
