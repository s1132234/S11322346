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

    // 遊戲成績
    var score by mutableStateOf(0)

    // 服務圖示資源 ID (假設我們有多個服務圖示，這裡只用 service0 作為範例)
    private val serviceDrawables = listOf(R.drawable.service0) // 您可以添加 service1, service2, ...

    // 服務圖示的當前資源 ID
    var currentServiceId by mutableStateOf(R.drawable.service0) // 初始設定為 service0

    // 服務圖示的垂直位置 (y 座標，以像素為單位)
    var serviceY by mutableFloatStateOf(0f)

    // 服務圖示的水平位置 (x 座標，以像素為單位，用於拖曳)
    var serviceX by mutableFloatStateOf(0f)

    // 紀錄螢幕的高度 (Px)，用於碰撞偵測
    var screenHeightPx by mutableFloatStateOf(0f)

    // 紀錄服務圖示的高度 (Px)，用於精準碰撞偵測
    var serviceImageHeightPx by mutableFloatStateOf(0f)

    private var dropJob: Job? = null

    init {
        // ViewModel 啟動時開始圖示的下落
        startDropping()
    }

    // 重設圖示位置並隨機選擇下一個圖示
    fun resetServiceIcon(screenWidthPx: Float) {
        // 隨機選擇圖示 (如果有多個 serviceDrawables)
        currentServiceId = serviceDrawables[Random.nextInt(serviceDrawables.size)]

        // 重設 Y 座標到螢幕上方
        serviceY = 0f

        // 重設 X 座標到水平中央
        serviceX = (screenWidthPx / 2f) - (serviceImageHeightPx / 2f)
    }

    // 開始下落的 Coroutine 任務
    private fun startDropping() {
        dropJob?.cancel() // 取消任何舊的任務
        dropJob = viewModelScope.launch {
            while (true) {
                delay(100L) // 0.1 秒 (100 毫秒)

                // 檢查是否已設定螢幕高度
                if (screenHeightPx > 0) {
                    val dropAmount = 20f // 下落 20 像素

                    // 計算新的 Y 座標
                    val newY = serviceY + dropAmount

                    // 碰撞偵測：圖示底部 (newY + 圖示高度) 是否超過螢幕底部
                    if (newY + serviceImageHeightPx >= screenHeightPx) {
                        // 碰撞螢幕下方，觸發重設
                        resetServiceIcon(screenHeightPx * 2) // 這裡傳入的參數實際上是screenWidth，但需要從外部傳入
                        // 注意：這裡暫時用 screenHeightPx * 2 替代，ExamScreen 必須在第一次呼叫時傳入正確的 screenWidth
                    } else {
                        serviceY = newY // 繼續下落
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        dropJob?.cancel() // ViewModel 銷毀時停止任務
    }
}