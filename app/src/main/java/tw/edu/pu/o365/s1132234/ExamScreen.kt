package tw.edu.pu.o365.s1132234

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun ExamScreen(viewModel: ExamViewModel = viewModel()) {

    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val width = displayMetrics.widthPixels.toFloat()
    val height = displayMetrics.heightPixels.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.happy),
            contentDescription = "Happy Image"
        )


        Text(
            text = "瑪利亞基金會服務大考驗",
            fontSize = 20.sp,
            )

        Text(
            text = "作者 : 資管二A 黃士豪",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "螢幕大小 : $width * $height",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "成績 : ${viewModel.score}分",
            fontSize = 18.sp
        )
    }
}