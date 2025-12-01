package tw.edu.pu.o365.s1132234

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ExamScreen(viewModel: ExamViewModel = viewModel()) {

    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val widthPx = displayMetrics.widthPixels.toFloat()
    val heightPx = displayMetrics.heightPixels.toFloat()
    val density = LocalDensity.current.density

    // --- 服務圖示的尺寸設定 (300px) ---
    val serviceImageSizePx = 300f
    val serviceImageSizeDp: Dp = (serviceImageSizePx / density).dp

    // 在 Composable 首次組合時，將螢幕尺寸和圖示尺寸傳遞給 ViewModel
    LaunchedEffect(Unit) {
        viewModel.screenHeightPx = heightPx
        viewModel.serviceImageHeightPx = serviceImageSizePx
        // 首次啟動時設定圖示位置
        viewModel.resetServiceIcon(widthPx)
    }

    // 將 ViewModel 中的像素座標轉換成 Dp 偏移，Compose 才能使用
    val serviceXInDp = (viewModel.serviceX / density).dp
    val serviceYInDp = (viewModel.serviceY / density).dp

    // --- 拖曳狀態管理 ---
    val draggableState = rememberDraggableState { delta ->
        // 限制拖曳範圍在螢幕邊界內
        val newX = viewModel.serviceX + delta
        val maxX = widthPx - serviceImageSizePx

        if (newX >= 0 && newX <= maxX) {
            viewModel.serviceX = newX
        }
    }


    // --- 原有圖片尺寸計算 ---
    val imageSizePx = 300f
    val imageSizeDp: Dp = (imageSizePx / density).dp
    val screenHalfHeightDp: Dp = (heightPx / density / 2f).dp
    val offsetForHalfScreen: Dp = screenHalfHeightDp - imageSizeDp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow)
    ) {

        // --- 四個固定角色圖示 ---
        Image(
            painter = painterResource(id = R.drawable.role0),
            contentDescription = "嬰兒",
            modifier = Modifier
                .size(imageSizeDp)
                .align(Alignment.TopStart)
                .offset(y = offsetForHalfScreen)
        )

        Image(
            painter = painterResource(id = R.drawable.role1),
            contentDescription = "兒童",
            modifier = Modifier
                .size(imageSizeDp)
                .align(Alignment.TopEnd)
                .offset(y = offsetForHalfScreen)
        )

        Image(
            painter = painterResource(id = R.drawable.role2),
            contentDescription = "成人",
            modifier = Modifier
                .size(imageSizeDp)
                .align(Alignment.BottomStart)
        )

        Image(
            painter = painterResource(id = R.drawable.role3),
            contentDescription = "一般民眾",
            modifier = Modifier
                .size(imageSizeDp)
                .align(Alignment.BottomEnd)
        )

        // --- 中央資訊區 ---
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.happy),
                contentDescription = "Happy Image"
            )

            Text(
                text = "瑪利亞基金會服務大考驗",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "作者 : 資管二A 黃士豪",
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "螢幕大小 : ${widthPx.toInt()} * ${heightPx.toInt()}",
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "成績 : ${viewModel.score}分",
                fontSize = 18.sp
            )
        }

        // --- 服務圖示 (位於最上層) ---
        Image(
            painter = painterResource(id = viewModel.currentServiceId),
            contentDescription = "服務圖示",
            modifier = Modifier
                .size(serviceImageSizeDp)
                // 應用 ViewModel 提供的 X 和 Y 偏移
                .offset(x = serviceXInDp, y = serviceYInDp)
                // 添加水平拖曳手勢
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                )
        )
    }
}