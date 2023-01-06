package com.ivy.formula.persistence.di

import android.content.Context
import com.ivy.formula.persistence.IvyWalletFormulaDb
import com.ivy.formula.persistence.dao.DataSourceDao
import com.ivy.formula.persistence.dao.FormulaDao
import com.ivy.formula.persistence.dao.ProjectDao
import com.ivy.formula.persistence.dao.ProjectFormulaLinkDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FormulaPersistenceModuleDI {
    @Singleton
    @Provides
    fun provideIvyWalletFormulaDb(
        @ApplicationContext appContext: Context
    ): IvyWalletFormulaDb = IvyWalletFormulaDb.create(appContext)

    @Provides
    fun provideFormulaDao(db: IvyWalletFormulaDb): FormulaDao = db.formulaDao()

    @Provides
    fun provideProjectDao(db: IvyWalletFormulaDb): ProjectDao = db.projectDao()

    @Provides
    fun provideProjectFormulaLinkDao(
        db: IvyWalletFormulaDb
    ): ProjectFormulaLinkDao = db.projectFormulaLinkDao()

    @Provides
    fun provideDataSourceDao(db: IvyWalletFormulaDb): DataSourceDao = db.dataSourceDao()
}