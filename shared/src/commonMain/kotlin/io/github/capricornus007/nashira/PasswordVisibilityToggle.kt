package io.github.capricornus007.nashira

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 密碼欄的顯示／隱藏切換按鈕。
 *
 * material-icons-core 只有 55 個圖示，沒有 Visibility / VisibilityOff，
 * 所以這裡用 Material Symbols 的原始 path 自建兩個 ImageVector。
 */
@Composable
fun PasswordVisibilityToggle(
    visible: Boolean,
    contentDescription: String,
    onToggle: () -> Unit,
) {
    IconButton(onClick = onToggle) {
        Icon(
            imageVector = if (visible) EyeOffIcon else EyeIcon,
            contentDescription = contentDescription,
        )
    }
}

/** Material Symbols `visibility`（睜眼）。 */
private val EyeIcon: ImageVector by lazy {
    materialIcon("Visibility") {
        // 眼形外框 + 中央瞳孔
        "M12,6.5c3.79,0 7.17,2.13 8.82,5.5 -1.65,3.37 -5.03,5.5 -8.82,5.5S4.83,15.37 3.18,12C4.83,8.63 8.21,6.5 12,6.5" +
            "M12,4.5C7,4.5 2.73,7.61 1,12c1.73,4.39 6,7.5 11,7.5s9.27,-3.11 11,-7.5c-1.73,-4.39 -6,-7.5 -11,-7.5" +
            "M12,9a3,3 0 1,0 0,6a3,3 0 1,0 0,-6"
    }
}

/** Material Symbols `visibility_off`（閉眼加斜線）。 */
private val EyeOffIcon: ImageVector by lazy {
    materialIcon("VisibilityOff") {
        "M12,6.5c3.79,0 7.17,2.13 8.82,5.5 -0.59,1.22 -1.42,2.27 -2.41,3.12l1.41,1.41" +
            "c1.39,-1.23 2.49,-2.77 3.18,-4.53 -1.73,-4.39 -6,-7.5 -11,-7.5 -1.4,0 -2.74,0.25 -3.98,0.7l1.55,1.55" +
            "C11.31,6.55 11.65,6.5 12,6.5" +
            "M2.71,3.16 L1.39,4.47l2.06,2.06C2.36,7.96 1.5,9.85 1,12c1.73,4.39 6,7.5 11,7.5 1.55,0 3.03,-0.3 4.38,-0.84" +
            "l2.15,2.15 1.32,-1.32z" +
            "M12,17.5c-3.79,0 -7.17,-2.13 -8.82,-5.5 0.44,-0.89 1.02,-1.69 1.7,-2.37l2.13,2.13" +
            "A4.99,4.99 0 0,0 12,17a4.9,4.9 0 0,0 1.72,-0.31l1.4,1.4c-1,0.27 -2.05,0.41 -3.12,0.41" +
            "M9.5,12a2.5,2.5 0 0,0 2.5,2.5c0.2,0 0.39,-0.03 0.58,-0.07l-3.01,-3.01C9.53,11.61 9.5,11.8 9.5,12"
    }
}

/** 用 24dp 視窗建一個實心填充的 ImageVector。 */
private inline fun materialIcon(name: String, pathData: () -> String): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        addPath(
            pathData = PathParser().parsePathString(pathData()).toNodes(),
            fill = SolidColor(Color.Black),
            stroke = null,
            strokeLineWidth = 0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
        )
    }.build()
