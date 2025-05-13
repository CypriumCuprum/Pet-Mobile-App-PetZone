package com.example.petapp.view.petHealth

import com.example.petapp.data.model.PetStatisticEntity
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Class này giúp xử lý dữ liệu thống kê thức ăn cho biểu đồ
 */
class FoodStatisticHelper {

    /**
     * Nhóm dữ liệu thức ăn theo ngày trong tuần
     */
    fun groupFoodStatisticsByDay(statisticsList: List<PetStatisticEntity>): Map<LocalDate, Float> {
        val result = mutableMapOf<LocalDate, Float>()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

        // Nhóm dữ liệu theo ngày và tính tổng lượng thức ăn
        statisticsList.forEach { statistic ->
            val localDateTime = LocalDateTime.parse(statistic.recorded_at + ":00", formatter)
//            val date = Instant.parse(statistic.recorded_at + ":00")
//                .atZone(ZoneId.systemDefault())
//                .toLocalDate()
            val date = localDateTime.atZone(ZoneId.systemDefault()).toLocalDate()

            // Cộng thêm giá trị vào ngày đã có, hoặc tạo mới nếu chưa có
            result[date] = (result[date] ?: 0f) + statistic.value
        }

        return result
    }

    /**
     * Lấy dữ liệu 7 ngày gần nhất
     */
    fun getLast7DaysData(groupedData: Map<LocalDate, Float>): List<Pair<LocalDate, Float>> {
        val today = LocalDate.now()
        val sevenDaysAgo = today.minusDays(6)

        // Tạo danh sách 7 ngày gần nhất
        val result = mutableListOf<Pair<LocalDate, Float>>()

        // Điền dữ liệu cho 7 ngày
        var currentDate = sevenDaysAgo
        while (!currentDate.isAfter(today)) {
            val value = groupedData[currentDate] ?: 0f
            result.add(Pair(currentDate, value))
            currentDate = currentDate.plusDays(1)
        }

        return result
    }

    /**
     * Lấy tên ngày trong tuần từ LocalDate
     */
    fun getDayName(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("EEE")
        return date.format(formatter)
    }
}