package com.flamyoad.a11y

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flamyoad.a11y.ui.theme.A11yTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A11yTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// https://dladukedev.com/articles/002_semanics_vs_clearandsetsemantics/
@Composable
fun MainScreen(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically)
    ) {

        // By using `clickable`, will implicitly use mergeDescendants = true, by grouping all elements together into one
        // https://developer.android.com/develop/ui/compose/accessibility/merging-clearing#merge-semantics
        Column(
            modifier = Modifier.clickable { }
        ) {
            Text("Extended Warranty", modifier = Modifier.semantics { heading() })
            Text("90 days for now")
        }

        // mergeDescendants = true, will group all elements together, and in turn clears the heading
        Column(
            modifier = Modifier.semantics(mergeDescendants = true) {}
        ) {
            Text("Extended Warranty", modifier = Modifier.semantics { heading() })
            Text("90 days for now")
        }

        // mergeDescendants = false, will treat each text as separate item, being said we have to swipe twice
        Column(
            modifier = Modifier.semantics(mergeDescendants = false) {}
        ) {
            Text("Extended Warranty", modifier = Modifier.semantics { heading() })
            Text("90 days for now")
        }

        // Because of `clickable` behavior of implicitly merging all elements.
        // Using mergeDescendants = false is useless here
        Column(
            modifier = Modifier
                .clickable { }
                .semantics(mergeDescendants = false) { role = Role.Button }
        ) {
            Text(
                "Extended Warranty",
                modifier = Modifier.semantics { heading() }
            )
            Text("90 days for now")
        }

        // Using clearAndSetSemantics will clear all existing elements' semantics,
        // Here, it will only pronounce "Button, double tap to activate"
        Column(
            modifier = Modifier
                .clickable { }
                .clearAndSetSemantics {
                    contentDescription = ""
                    role = Role.Button
                }
        ) {
            Text("Extended Warranty",
                modifier = Modifier.semantics { heading() })
            Text("90 days for now")
        }

        // Very very final solution,
        // This means Talkback will pronounce heading and text separately, but both elements can still trigger onTap via Talkback
        Column(
            modifier = Modifier.clickable {
                printNoobs()
            }
        ) {
            Text("Extended Warranty",
                modifier = Modifier.semantics(mergeDescendants = true) {
                    heading()
                    onClick { // This will pronounce `double tap to activate` too
                        printNoobs()
                        true
                    }
                })

            // After swiping the header, we reach here, the accesibility box will surround whole Column,
            // but Talkback will only read below 2 texts
            Text("90 days for now")
            Text("Can't be serious bro")
        }
    }
}


fun printNoobs() {
    println("println")
}