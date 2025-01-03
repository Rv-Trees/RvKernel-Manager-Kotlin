package com.rve.rvkernelmanager.utils

import java.io.File
import com.topjohnwu.superuser.Shell
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun isRooted(): Boolean {
    return java.lang.Boolean.TRUE == Shell.isAppGrantedRoot()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoRootDialog(onConfirm: () -> Unit) {
    val openDialog = remember { mutableStateOf(true) }
    
    if (openDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {}
        ) {
            Surface(
                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation,
            ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "RvKernel Manager requires root access!",
		        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = {
                            openDialog.value = false
                            onConfirm()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Exit")
                    }
                }
            }
        }
    }
}

fun testFile(filePath: String): Boolean {
    return if (File(filePath).exists()) {
        true
    } else {
        Shell.cmd("test -f $filePath && echo true || echo false")
            .exec()
            .out[0] == "true"
    }
}

fun setPermissions(permission: Int, filePath: String) {
    Shell.cmd("chmod $permission $filePath").exec()
}

fun readFile(filePath: String): String {
    return try {
        val result = Shell.cmd("cat $filePath").exec()
        if (result.isSuccess) {
            result.out.joinToString("\n").trim()
        } else {
            Log.e("ReadFile", "Failed to read file at $filePath: ${result.err}")
            ""
        }
    } catch (e: Exception) {
        Log.e("ReadFile", "Error executing shell command for $filePath: ${e.message}", e)
        ""
    }
}

fun writeFile(filePath: String, value: String): Boolean {
    return try {
        val command = "echo $value > $filePath"
        val result = Shell.cmd(command).exec()
        if (result.isSuccess) true else {
            Log.e("WriteFile", "Failed to write file at $filePath: ${result.err}")
            false
        }
    } catch (e: Exception) {
        Log.e("WriteFile", "Error executing shell command for $filePath: ${e.message}", e)
        false
    }
}
