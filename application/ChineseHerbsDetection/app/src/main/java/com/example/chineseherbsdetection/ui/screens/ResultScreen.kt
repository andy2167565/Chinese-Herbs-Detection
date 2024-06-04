package com.example.chineseherbsdetection.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.chineseherbsdetection.R
import com.example.chineseherbsdetection.model.Score
import com.example.chineseherbsdetection.ui.theme.ChineseHerbsDetectionTheme
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Base64

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ResultScreen(
    imageUri: Uri,
    onNextButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    azureViewModel: AzureViewModel = viewModel(factory = AzureViewModel.Factory)
) {
    val context = LocalContext.current

    // Convert image URI to base64
    val imageBase64 = context.uriToBase64(imageUri)

    // Send HTTP request
    azureViewModel.getPredictScore(imageBase64)
    when (val azureUiState = azureViewModel.azureUiState) {
        is AzureUiState.Loading -> LoadingScreen(modifier.fillMaxSize())
        is AzureUiState.Success -> HerbInfoScreen(
            azureUiState.results,
            imageUri,
            onNextButtonClicked,
            modifier = modifier.fillMaxWidth()
        )
        is AzureUiState.Error -> ErrorScreen(
            { azureViewModel.getPredictScore(imageBase64) },
            onNextButtonClicked,
            modifier = modifier.fillMaxSize()
        )
    }
}

/**
 * The result screen displaying herb info.
 */
@Composable
fun HerbInfoScreen(
    results: Score,
    imageUri: Uri,
    onNextButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val confidence = results.confidence.toDouble()

    Box(
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = dimensionResource(R.dimen.vertical_padding))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUri)
                    .build(),
                modifier = Modifier
                    .fillMaxSize(),
                contentDescription = stringResource(R.string.image_content_description),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.vertical_padding)))
            // Check confidence threshold
            if (results.sciName == "Unknown" || confidence <= 50.0) {
                Card(
                    border = BorderStroke(
                        dimensionResource(R.dimen.card_border),
                        colorResource(R.color.theme)
                    ),
                    colors = CardDefaults.cardColors(containerColor = colorResource(R.color.text)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.card_inner_padding)))
                    Text(
                        text = "The object is not in the current knowledge base.\n" +
                                "Please try again with another item.",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Justify,
                        color = Color.Black,
                        modifier = Modifier.padding(
                            start = dimensionResource(R.dimen.card_inner_padding),
                            end = dimensionResource(R.dimen.card_inner_padding)
                        )
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.card_inner_padding)))
                }
            } else {
                InfoCard(
                    confidence,
                    results.chiName,
                    results.sciName,
                    results.category,
                    results.efficacy
                )
            }
            ActionButton(
                imageResourceId = R.drawable.add_photo_alternate,
                labelResourceId = R.string.another,
                enabled = true,
                onClick = {
                    // Navigate to the get image page
                    onNextButtonClicked()
                }
            )
        }
    }
}

/**
 * The result screen displaying the loading message.
 */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading),
        modifier = modifier.size(dimensionResource(R.dimen.loading_size))
    )
}

/**
 * The result screen displaying error message with re-attempt button.
 */
@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    onNextButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = dimensionResource(R.dimen.vertical_padding))
        ) {
            Image(
                painter = painterResource(R.drawable.ic_connection_error),
                contentDescription = stringResource(R.string.fail)
            )
            Text(
                text = stringResource(R.string.fail),
                style = MaterialTheme.typography.titleLarge
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimensionResource(R.dimen.vertical_padding))
        ) {
            ActionButton(
                imageResourceId = R.drawable.refresh,
                labelResourceId = R.string.retry,
                enabled = true,
                onClick = {
                    retryAction()
                }
            )
            ActionButton(
                imageResourceId = R.drawable.home,
                labelResourceId = R.string.homepage,
                enabled = true,
                onClick = {
                    // Navigate to the get image page
                    onNextButtonClicked()
                }
            )
        }
    }
}

@Composable
fun DataRow(
    @StringRes itemResourceId: Int,
    data: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(
            start = dimensionResource(R.dimen.card_inner_padding),
            end = dimensionResource(R.dimen.card_inner_padding)
        )
    ) {
        Text(
            text = stringResource(id = itemResourceId),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = modifier.weight(1.5f)
        )
        Text(
            text = data,
            style = MaterialTheme.typography.bodyLarge,
//            textAlign = TextAlign.Justify,
            color = Color.Black,
            modifier = modifier.weight(2f)
        )
    }
}

@Composable
fun InfoCard(
    confidence: Double,
    chiName: String,
    sciName: String,
    category: String,
    efficacy: String,
    modifier: Modifier = Modifier
) {
    Card(
        border = BorderStroke(
            dimensionResource(R.dimen.card_border),
            colorResource(R.color.theme)
        ),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.text)),
        modifier = modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.card_inner_padding)))
        DataRow(
            R.string.confidence,
            BigDecimal(confidence).setScale(1, RoundingMode.HALF_EVEN).toString() + "%"
        )
        DataRow(R.string.chi_name, chiName)
        DataRow(R.string.sci_name, sciName)
        DataRow(R.string.category, category)
        DataRow(R.string.efficacy, efficacy)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.card_inner_padding)))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Context.uriToBase64(uri: Uri): String {
    // URI to bitmap
    val imageBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
    // Resize the bitmap
//    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 224, 224, false)
    val baos = ByteArrayOutputStream()
    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val ba = baos.toByteArray()
    return Base64.getEncoder().encodeToString(ba)
}

@Preview(showBackground = true)
@Composable
fun ResultPreview() {
    ChineseHerbsDetectionTheme {
        HerbInfoScreen(
            results = Score(
                "100.0",
                "洋金花",
                "Daturae Flos",
                "Flowers",
                "平喘止咳，解痙定痛。用於哮喘咳嗽， 脘腹冷痛，風濕痹痛，小兒慢驚；外科麻醉。"
            ),
            imageUri = Uri.EMPTY,
            onNextButtonClicked = {},
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    ChineseHerbsDetectionTheme {
        LoadingScreen(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    ChineseHerbsDetectionTheme {
        ErrorScreen(
            retryAction = {},
            onNextButtonClicked = {},
            modifier = Modifier
                .fillMaxSize()
        )
    }
}