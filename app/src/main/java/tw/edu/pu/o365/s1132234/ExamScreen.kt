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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
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

    val collisionAreas = remember { mutableStateMapOf<String, CollisionArea>() }

    val serviceImageSizePx = 300f
    val serviceImageSizeDp: Dp = (serviceImageSizePx / density).dp

    LaunchedEffect(Unit) {
        viewModel.screenHeightPx = heightPx
        viewModel.serviceImageHeightPx = serviceImageSizePx
        viewModel.initializeIcon(widthPx)
    }

    // 檢查服務圖示是否碰撞任何角色圖示
    LaunchedEffect(viewModel.serviceY, viewModel.isDropping) {
        // 只有在圖示正在下落時才檢查碰撞
        if (viewModel.serviceY > 0 && viewModel.isDropping) {
            val serviceRect = RectF(
                viewModel.serviceX,
                viewModel.serviceY,
                viewModel.serviceX + serviceImageSizePx,
                viewModel.serviceY + serviceImageSizePx
            )

            for ((key, area) in collisionAreas) {
                if (serviceRect.intersects(area.rect)) {
                    viewModel.score += 10
                    // *** 修正後的碰撞訊息 ***
                    viewModel.handleCollision(" (碰撞${area.description})", widthPx)
                    break
                }
            }
        }
    }


    val serviceXInDp = (viewModel.serviceX / density).dp
    val serviceYInDp = (viewModel.serviceY / density).dp

    val draggableState = rememberDraggableState { delta ->
        val newX = viewModel.serviceX + delta
        val maxX = widthPx - serviceImageSizePx

        if (newX >= 0 && newX <= maxX) {
            viewModel.serviceX = newX
        }
    }

    val imageSizePx = 300f
    val imageSizeDp: Dp = (imageSizePx / density).dp
    val screenHalfHeightDp: Dp = (heightPx / density / 2f).dp
    val offsetForHalfScreen: Dp = screenHalfHeightDp - imageSizeDp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow)
    ) {

        // 四個固定角色圖示 (修正 CollisionArea 的 description)
        Image(
            painter = painterResource(id = R.drawable.role0),
            contentDescription = "嬰兒",
            modifier = Modifier
                .size(imageSizeDp)
                .align(Alignment.TopStart)
                .offset(y = offsetForHalfScreen)
                .onGloballyPositioned { coordinates ->
                    val rect = coordinates.boundsInRoot()
                    collisionAreas["role0"] = CollisionArea(
                        RectF(rect.left, rect.top, rect.right, rect.bottom),
                        "嬰幼兒圖示" // <-- 修正
                    )
                }
        )

        Image(
            painter = painterResource(id = R.drawable.role1),
            contentDescription = "兒童",
            modifier = Modifier
                .size(imageSizeDp)
                .align(Alignment.TopEnd)
                .offset(y = offsetForHalfScreen)
                .onGloballyPositioned { coordinates ->
                    val rect = coordinates.boundsInRoot()
                    collisionAreas["role1"] = CollisionArea(
                        RectF(rect.left, rect.top, rect.right, rect.bottom),
                        "兒童圖示" // <-- 修正
                    )
                }
        )

        Image(
            painter = painterResource(id = R.drawable.role2),
            contentDescription = "成人",
            modifier = Modifier
                .size(imageSizeDp)
                .align(Alignment.BottomStart)
                .onGloballyPositioned { coordinates ->
                    val rect = coordinates.boundsInRoot()
                    collisionAreas["role2"] = CollisionArea(
                        RectF(rect.left, rect.top, rect.right, rect.bottom),
                        "成人圖示" // <-- 修正
                    )
                }
        )

        Image(
            painter = painterResource(id = R.drawable.role3),
            contentDescription = "一般民眾",
            modifier = Modifier
                .size(imageSizeDp)
                .align(Alignment.BottomEnd)
                .onGloballyPositioned { coordinates ->
                    val rect = coordinates.boundsInRoot()
                    collisionAreas["role3"] = CollisionArea(
                        RectF(rect.left, rect.top, rect.right, rect.bottom),
                        "一般民眾圖示" // <-- 已包含
                    )
                }
        )

        // 中央資訊區
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

            // 分數顯示，加入 collisionMessage
            Text(
                text = "成績 : ${viewModel.score}分${viewModel.collisionMessage}",
                fontSize = 18.sp
            )
        }

        // 服務圖示 (位於最上層)
        Image(
            painter = painterResource(id = viewModel.currentServiceId),
            contentDescription = "服務圖示",
            modifier = Modifier
                .size(serviceImageSizeDp)
                .offset(x = serviceXInDp, y = serviceYInDp)
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                )
        )
    }
}