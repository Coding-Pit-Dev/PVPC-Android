package com.codingpit.pvpcplanner.data.local.sources

import com.codingpit.pvpcplanner.data.local.db.PVPCDao
import com.codingpit.pvpcplanner.data.mappers.toDomain
import com.codingpit.pvpcplanner.data.mappers.toEntity
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import javax.inject.Inject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DefaultPriceLocalDataSource @Inject constructor(
    private val pvpcDao: PVPCDao,
) : PriceLocalDataSource {
    private val queryDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override suspend fun getPrices(date: String): List<PVPCModel> {
        val parsedDate = LocalDate.parse(date).format(queryDateFormatter)
        return pvpcDao.getPrices(parsedDate).map { it.toDomain() }
    }

    override suspend fun savePrices(pvpcModel: List<PVPCModel>) {
        pvpcDao.insertAll(pvpcModel.map { it.toEntity() })
    }
}
