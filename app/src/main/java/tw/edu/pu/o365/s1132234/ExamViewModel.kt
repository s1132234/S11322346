package tw.edu.pu.o365.s1132234

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class ExamViewModel : ViewModel() {

    var score by mutableStateOf(0)
    var collisionMessage by mutableStateOf("")

    private val serviceDrawables = listOf(
        R.drawable.service0,
        R.drawable.service1,
        R.drawable.service2,
        R.drawable.service3
    )

    var currentServiceId by mutableStateOf(R.drawable.service0)

    var serviceY by mutableFloatStateOf(0f)

    var serviceX by mutableFloatStateOf(0f)

    var screenHeightPx by mutableFloatStateOf(0f)

    var screenWidthPx by mutableFloatStateOf(0f)  // ✅ 新增:儲存螢幕寬度

    var serviceImageHeightPx by mutableFloatStateOf(0f)

    var isDropping by mutableStateOf(true)

    private var dropJob: Job? = null
    private var delayJob: Job? = null

    init {
        startDropping()
    }

    // 負責圖示從上方重新開始下落的邏輯
    fun restartDrop(screenWidthPx: Float) {
        delayJob?.cancel()
        serviceY = 0f
        collisionMessage = ""
        isDropping = true
    }

    // 處理碰撞/觸底後的動作:設定訊息,停止下落,並開始延遲計時
    fun handleCollision(message: String, screenWidthPx: Float) {
        if (!isDropping) return

        collisionMessage = message
        isDropping = false

        // 啟動 3 秒延遲
        delayJob?.cancel()
        delayJob = viewModelScope.launch {
            delay(3000L) // 延遲 3 秒
            // 延遲結束後,重新初始化圖示 (包括隨機選擇 ID 和重設X位置)
            initializeIcon(screenWidthPx)
            restartDrop(screenWidthPx) // 延遲結束後重新開始下落
        }
    }

    // 首次啟動時設定圖示位置和 ID
    fun initializeIcon(screenWidthPx: Float) {
        currentServiceId = serviceDrawables[Random.nextInt(serviceDrawables.size)]
        this.screenWidthPx = screenWidthPx  // ✅ 儲存螢幕寬度
        serviceX = (screenWidthPx / 2f) - (serviceImageHeightPx / 2f)
    }

    private fun startDropping() {
        dropJob?.cancel()
        dropJob = viewModelScope.launch {
            while (true) {
                delay(100L) // 0.1 秒

                if (screenHeightPx > 0 && isDropping) {
                    val dropAmount = 20f

                    serviceY += dropAmount

                    // 檢查是否碰撞底邊界
                    if (serviceY + serviceImageHeightPx >= screenHeightPx) {

                        // 1. 確保 Y 座標不會超過底部 (防止圖示在下一幀跳出畫面)
                        serviceY = screenHeightPx - serviceImageHeightPx

                        // 2. 扣分
                        score -= 1

                        // 3. 處理 3 秒延遲和重啟
                        handleCollision(" (掉到最下方)", screenWidthPx)  // ✅ 使用儲存的 screenWidthPx
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        dropJob?.cancel()
        delayJob?.cancel()
    }
}

// 用於碰撞偵測的簡單矩形類別
data class RectF(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    fun intersects(other: RectF): Boolean {
        return left < other.right && right > other.left && top < other.bottom && bottom > other.top
    }
}

// 用於儲存角色位置和描述的資料類別
data class CollisionArea(val rect: RectF, val description: String, val roleId: Int)