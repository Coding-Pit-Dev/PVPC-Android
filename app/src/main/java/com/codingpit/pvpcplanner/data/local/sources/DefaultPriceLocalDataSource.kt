package com.codingpit.pvpcplanner.data.local.sources

import com.codingpit.pvpcplanner.data.local.db.PVPCDao
import com.codingpit.pvpcplanner.data.mappers.toDomain
import com.codingpit.pvpcplanner.data.mappers.toEntity
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import javax.inject.Inject

class DefaultPriceLocalDataSource @Inject constructor(
    private val pvpcDao: PVPCDao,
) : PriceLocalDataSource {
    override suspend fun getPrices(date: String): List<PVPCModel> {
        val parsedDate = date.split("-").reversed().joinToString("/")
        return pvpcDao.getPrices(parsedDate).map { it.toDomain() }
    }

    override suspend fun savePrices(pcpcModel: List<PVPCModel>) {
        pvpcDao.insertAll(pcpcModel.map { it.toEntity() })
    }
}
