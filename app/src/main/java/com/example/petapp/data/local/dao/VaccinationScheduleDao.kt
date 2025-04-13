package com.example.petapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.petapp.data.model.VaccinationScheduleEntity
import java.util.Date

/**
 * Data Access Object (DAO) cho bảng vaccination_schedule.
 * Cung cấp các phương thức truy cập, thêm, sửa, xóa dữ liệu trong bảng.
 */
@Dao
interface VaccinationScheduleDao {

    /**
     * Thêm mới một lịch tiêm vào cơ sở dữ liệu.
     * @param schedule Thông tin lịch tiêm cần thêm
     * @return ID của lịch tiêm vừa được thêm
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: VaccinationScheduleEntity): Long

    /**
     * Cập nhật thông tin của một lịch tiêm đã tồn tại.
     * @param schedule Thông tin lịch tiêm cần cập nhật
     */
    @Update
    suspend fun updateSchedule(schedule: VaccinationScheduleEntity)

    /**
     * Xóa một lịch tiêm khỏi cơ sở dữ liệu.
     * @param schedule Lịch tiêm cần xóa
     */
    @Delete
    suspend fun deleteSchedule(schedule: VaccinationScheduleEntity)

    /**
     * Lấy tất cả lịch tiêm, sắp xếp theo ngày và giờ tiêm.
     * @return LiveData chứa danh sách lịch tiêm
     */
    @Query("SELECT * FROM vaccination_schedule ORDER BY vaccination_date, vaccination_time")
    fun getAllSchedules(): LiveData<List<VaccinationScheduleEntity>>

    /**
     * Lấy danh sách lịch tiêm theo trạng thái, sắp xếp theo ngày và giờ tiêm.
     * @param status Trạng thái lịch tiêm cần tìm
     * @return LiveData chứa danh sách lịch tiêm theo trạng thái
     */
    @Query("SELECT * FROM vaccination_schedule WHERE status = :status ORDER BY vaccination_date, vaccination_time")
    fun getSchedulesByStatus(status: String): LiveData<List<VaccinationScheduleEntity>>

    /**
     * Tìm kiếm lịch tiêm theo ID.
     * @param scheduleId ID của lịch tiêm cần tìm
     * @return Lịch tiêm tìm được hoặc null nếu không tồn tại
     */
    @Query("SELECT * FROM vaccination_schedule WHERE schedule_id = :scheduleId")
    suspend fun getScheduleById(scheduleId: Long): VaccinationScheduleEntity?

    /**
     * Lấy danh sách lịch tiêm theo ID thú cưng.
     * @param petId ID của thú cưng
     * @return LiveData chứa danh sách lịch tiêm của thú cưng
     */
    @Query("SELECT * FROM vaccination_schedule WHERE pet_id = :petId ORDER BY vaccination_date, vaccination_time")
    fun getSchedulesByPetId(petId: Long): LiveData<List<VaccinationScheduleEntity>>

    /**
     * Lấy danh sách lịch tiêm sắp tới (trong tương lai).
     * @param currentDate Ngày hiện tại để so sánh
     * @return LiveData chứa danh sách lịch tiêm sắp tới
     */
    @Query("SELECT * FROM vaccination_schedule WHERE vaccination_date >= :currentDate AND status = 'UPCOMING' ORDER BY vaccination_date, vaccination_time")
    fun getUpcomingSchedules(currentDate: Date): LiveData<List<VaccinationScheduleEntity>>

    /**
     * Lấy danh sách lịch tiêm cần nhắc nhở trong khoảng thời gian tới.
     * @param startDate Ngày bắt đầu khoảng thời gian
     * @param endDate Ngày kết thúc khoảng thời gian
     * @return LiveData chứa danh sách lịch tiêm trong khoảng thời gian
     */
    @Query("SELECT * FROM vaccination_schedule WHERE vaccination_date BETWEEN :startDate AND :endDate AND status = 'UPCOMING' ORDER BY vaccination_date, vaccination_time")
    fun getSchedulesForReminder(startDate: Date, endDate: Date): LiveData<List<VaccinationScheduleEntity>>
}