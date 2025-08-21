package io.homeassistant.companion.android.settings.wear.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.homeassistant.companion.android.common.R as commonR
import io.homeassistant.companion.android.settings.wear.SettingsWearViewModel
import timber.log.Timber

@Composable
fun SettingsWearOnboardingView(
    settingsWearViewModel: SettingsWearViewModel,
    onInstallOnWearDeviceClicked: () -> Unit,
    onFinishInstallOnDevices: () -> Unit,
    onBackClicked: () -> Unit,
) {
    var infoTextResource by remember { mutableIntStateOf(0) }
    var shouldDisplayRemoteAppInstallButton by remember { mutableStateOf(false) }
    val wearNodesWithApp by settingsWearViewModel.wearNodesWithApp.collectAsStateWithLifecycle()
    val allConnectedNodes by settingsWearViewModel.allConnectedNodes.collectAsStateWithLifecycle()
    when {
        wearNodesWithApp == null || allConnectedNodes == null -> {
            Timber.d("Waiting on Results for both connected nodes and nodes with app")
            infoTextResource = commonR.string.message_checking
            shouldDisplayRemoteAppInstallButton = true
        }

        allConnectedNodes?.isEmpty() == true -> {
            Timber.d("No devices")
            infoTextResource = commonR.string.message_no_connected_nodes
            shouldDisplayRemoteAppInstallButton = true
        }

        wearNodesWithApp?.isEmpty() == true -> {
            Timber.d("Missing on all devices")
            infoTextResource = commonR.string.message_missing_all
            shouldDisplayRemoteAppInstallButton = true
        }

        (wearNodesWithApp?.size ?: 0) < (allConnectedNodes?.size ?: 0) -> {
            Timber.d("Installed on some devices")
            onFinishInstallOnDevices.invoke()
        }

        else -> {
            Timber.d("Installed on all devices")
            onFinishInstallOnDevices.invoke()
        }
    }

    SettingsWearOnboardingViewContent(
        infoTextResource,
        shouldDisplayRemoteAppInstallButton,
        onInstallOnWearDeviceClicked,
        onBackClicked,
    )
}

@Composable
fun SettingsWearOnboardingViewContent(
    infoTextTitleResource: Int,
    shouldDisplayRemoteAppInstallButton: Boolean,
    onInstallOnWearDeviceClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            SettingsWearTopAppBar(
                title = {
                    Text(
                        stringResource(commonR.string.wear_os_settings_title),
                        fontWeight = FontWeight.Bold,
                    )
                },
                onBackClicked = onBackClicked,
                docsLink = WEAR_DOCS_LINK,
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(modifier = Modifier.padding(start = 15.dp, top = 50.dp, end = 15.dp)) {
                Text(
                    text = stringResource(infoTextTitleResource),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        color = colorResource(commonR.color.colorHeadline1),
                    ),
                )
            }

            if (shouldDisplayRemoteAppInstallButton) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Button(
                        onClick = onInstallOnWearDeviceClicked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 70.dp)
                            .weight(1f),
                    ) {
                        Text(
                            text = stringResource(commonR.string.install_app).uppercase(),
                            letterSpacing = 1.sp,
                            style = MaterialTheme.typography.body2,
                            color = colorResource(commonR.color.colorBackground),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSettingWearAppMissingViewWithButton() {
    SettingsWearOnboardingViewContent(
        infoTextTitleResource = commonR.string.message_checking,
        shouldDisplayRemoteAppInstallButton = false,
        onInstallOnWearDeviceClicked = {},
        onBackClicked = {},
    )
}

@PreviewLightDark
@Composable
private fun PreviewSettingWearAppMissingView() {
    SettingsWearOnboardingViewContent(
        infoTextTitleResource = commonR.string.message_checking,
        shouldDisplayRemoteAppInstallButton = true,
        onInstallOnWearDeviceClicked = {},
        onBackClicked = {},
    )
}
