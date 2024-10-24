package com.example.chineseherbsdetection.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.chineseherbsdetection.R
import com.example.chineseherbsdetection.ui.theme.ChineseHerbsDetectionTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@Composable
fun GetImageScreen(
    onNextButtonClicked: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val galleryUri = "image/*"

    var cameraUri by rememberSaveable {
        mutableStateOf<Uri>(Uri.EMPTY)
    }
    var imageUri by rememberSaveable {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    // Select image from gallery
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
        }
    }

    // Take photo
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = cameraUri
        }
    }

    // Request Read Permission
    val readPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            imagePicker.launch(galleryUri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Request Camera Permission
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraUri = context.createImageFile()
            cameraLauncher.launch(cameraUri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier
    ) {
        if (imageUri.toString().isEmpty()) {
            Text(
                text = stringResource(R.string.upload),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )
        } else {
            // Create AsyncImage object if image exists
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUri)
                    .crossfade(true)
                    .build(),
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter),
                contentDescription = stringResource(R.string.image_content_description),
                contentScale = ContentScale.Crop
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimensionResource(R.dimen.vertical_padding))
        ) {
            // Select image from gallery
            ActionButton(
                imageResourceId = R.drawable.photo_library,
                labelResourceId = R.string.gallery,
                enabled = true,
                onClick = {
                    // Check SDK version
                    val permission = when (Build.VERSION.SDK_INT) {
                        in 1..32 -> Manifest.permission.READ_EXTERNAL_STORAGE
                        else -> Manifest.permission.READ_MEDIA_IMAGES
                    }
                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(
                            context,
                            permission
                        )
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        imagePicker.launch(galleryUri)
                    } else {
                        // Request a permission
                        readPermissionLauncher.launch(permission)
                    }
                }
            )
            // Take photo
            ActionButton(
                imageResourceId = R.drawable.photo_camera,
                labelResourceId = R.string.camera,
                enabled = true,
                onClick = {
                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        )
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraUri = context.createImageFile()
                        cameraLauncher.launch(cameraUri)
                    } else {
                        // Request a permission
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            )
            // Upload image
            ActionButton(
                imageResourceId = R.drawable.cloud_upload,
                labelResourceId = R.string.analyze,
                // The button is enabled when the user selects an image
                enabled = imageUri.path?.isNotEmpty() == true,
                onClick = {
                    // Navigate to the result page
                    onNextButtonClicked(imageUri)
                }
            )
        }
    }
}

@Composable
fun ActionButton(
    @DrawableRes imageResourceId: Int,
    @StringRes labelResourceId: Int,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        enabled = enabled,
        onClick = onClick,
        shape = RoundedCornerShape(dimensionResource(R.dimen.button_radius)),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.theme)),
        modifier = modifier
            .width(dimensionResource(R.dimen.button_width))
            .height(dimensionResource(R.dimen.button_height))
            .padding(top = dimensionResource(R.dimen.button_padding))
    ) {
        Image(
            painter = painterResource(imageResourceId),
            contentDescription = null,
            modifier = Modifier.weight(0.5f)
        )
        Text(
            text = stringResource(labelResourceId),
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.5f)
        )
    }
}

private fun Context.createImageFile(): Uri {
    val directory = File(filesDir, "images")
    if (!directory.exists()) {
        directory.mkdirs()
    }
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val file = File.createTempFile(
        "image_${timeStamp}_",
        ".png",
        directory
    )
    return FileProvider.getUriForFile(
        Objects.requireNonNull(this),
        "$packageName.FileProvider",
        file
    )
}

@Preview(showBackground = true)
@Composable
fun GetImagePreview() {
    ChineseHerbsDetectionTheme {
        GetImageScreen(
            onNextButtonClicked = {},
            modifier = Modifier
                .fillMaxSize()
        )
    }
}