package com.codingpit.pvpcplanner.data.local.sources

import com.codingpit.pvpcplanner.data.local.db.PVPCDao
import com.codingpit.pvpcplanner.data.mappers.toDomain
import com.codingpit.pvpcplanner.data.mappers.toEntity
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DefaultPriceLocalDataSource @Inject constructor(
    private val pvpcDao: PVPCDao,
) : PriceLocalDataSource {

    override suspend fun getPrices(date: String): List<PVPCModel> {
        return try {
            val formattedDate = LocalDate.parse(date, inputDateFormatter).format(queryDateFormatter)
            pvpcDao.getPrices(formattedDate).map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun savePrices(pvpcModel: List<PVPCModel>) {
        pvpcDao.insertAll(pvpcModel.map { it.toEntity() })
    }

    companion object {
        private val queryDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        private val inputDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    }
}
