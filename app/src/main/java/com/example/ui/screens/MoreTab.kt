package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardDarkBg
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.RedAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreTab(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Screen states
    var activeSubPage by remember { mutableStateOf<SubPage?>(null) }

    // Forms state
    var selectedChannelName by remember { mutableStateOf("") }
    var brokenDescription by remember { mutableStateOf("") }
    var supportSubject by remember { mutableStateOf("") }
    var supportMessage by remember { mutableStateOf("") }

    val activePage = activeSubPage

    if (activePage != null) {
        // Render sub-screens with simple back mechanics
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { activeSubPage = null }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = GreenPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = activePage.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(color = GlassBorder)

            when (activePage) {
                SubPage.ReportBroken -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "কোনো লাইভ চ্যানেলে সমস্যা দেখা দিলে আমাদের জানান! আমরা দ্রুত সমাধান করার চেষ্টা করব।",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        OutlinedTextField(
                            value = selectedChannelName,
                            onValueChange = { selectedChannelName = it },
                            label = { Text("চ্যানেলের নাম (Channel Name)") },
                            modifier = Modifier.fillMaxWidth().testTag("broken_channel_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = GreenPrimary,
                                unfocusedLabelColor = Color.Gray,
                                cursorColor = GreenPrimary
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = brokenDescription,
                            onValueChange = { brokenDescription = it },
                            label = { Text("সমস্যাটির সংক্ষিপ্ত বিবরণ (Describe issue)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("broken_desc_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = GreenPrimary,
                                unfocusedLabelColor = Color.Gray,
                                cursorColor = GreenPrimary
                            ),
                            maxLines = 5
                        )

                        Button(
                            onClick = {
                                if (selectedChannelName.isBlank()) {
                                    Toast.makeText(context, "অনুগ্রহ করে চ্যানেলের নাম লিখুন", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "রিপোর্ট সফলভাবে জমা দেওয়া হয়েছে! ধন্যবাদ।", Toast.LENGTH_LONG).show()
                                    selectedChannelName = ""
                                    brokenDescription = ""
                                    activeSubPage = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RedAccent),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_report_button")
                        ) {
                            Text("রিপোর্ট সাবমিট করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                SubPage.ContactSupport -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "আমাদের সাপোর্ট টিমের সাথে সরাসরি যোগাযোগের জন্য নিচের ফর্মটি ব্যবহার করুন।",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        OutlinedTextField(
                            value = supportSubject,
                            onValueChange = { supportSubject = it },
                            label = { Text("বিষয় (Subject)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = GreenPrimary,
                                unfocusedLabelColor = Color.Gray,
                                cursorColor = GreenPrimary
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = supportMessage,
                            onValueChange = { supportMessage = it },
                            label = { Text("আপনার বার্তা (Your message)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = GreenPrimary,
                                unfocusedLabelColor = Color.Gray,
                                cursorColor = GreenPrimary
                            ),
                            maxLines = 5
                        )

                        Button(
                            onClick = {
                                if (supportSubject.isBlank() || supportMessage.isBlank()) {
                                    Toast.makeText(context, "সবগুলো ঘর পূরণ করুন", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "বার্তা পাঠানো হয়েছে! Support ID: #${System.currentTimeMillis().toString().takeLast(6)}", Toast.LENGTH_LONG).show()
                                    supportSubject = ""
                                    supportMessage = ""
                                    activeSubPage = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("বার্তা পাঠান", fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                            border = BorderStroke(1.dp, GlassBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "সরাসরি ইমেইল ঠিকানা",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "সাপোর্ট ইমেইল: support@kheladekhun.live",
                                    color = GreenPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "সাপ্তাহিক ৭ দিনই সকাল ১০ টা থেকে রাত ১০ টা পর্যন্ত টিম কার্যকর থাকে।",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                SubPage.AboutApp -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                            border = BorderStroke(1.dp, GlassBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "খেলা দেখুন (Khela Dekhun) v1.0",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "খেলা দেখুন একটি প্রিমিয়াম লাইভ স্পোর্টস ও আইপিটিভি (IPTV) স্ট্রিমিং অ্যাপ্লিকেশন। এর সাহায্যে বিশ্বের বিভিন্ন দেশের সেরা স্পোর্টস ও বিনোদনমূলক লাইভ টিভি চ্যানেলগুলো এক ক্লিকে বাফারিং ছাড়াই খেলা দেখতে পারবেন।",
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Text(
                            text = "বৈশিষ্ট্যসমূহ:",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        val features = listOf(
                            "⚡ সুপার ফাস্ট লাইভ বাফারিং স্পিড",
                            "🇧🇩 বাংলাদেশসহ ৯টি দেশের IPTV চ্যানেল সিঙ্ক",
                            "🖥️ পিকচার-ইন-পিকচার (PIP) মুড সাপোর্ট",
                            "❤️ প্রিয় চ্যানেল পছন্দ করে আলাদা সুচিপত্র তৈরি",
                            "📊 লাইভ দর্শক ও হিটকাউন্টার রিয়েল টাইম আপডেট"
                        )

                        features.forEach { feature ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = feature,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                SubPage.PrivacyPolicy -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "সর্বশেষ আপডেট: জুন ২০২৬",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "আমাদের 'খেলা দেখুন' অ্যাপ্লিকেশনটি ব্যবহার করার জন্য আপনাকে ধন্যবাদ। আপনার তথ্যের সুরক্ষা নিশ্চিতকরণ ও চমৎকার সার্ভিস অভিজ্ঞতা প্রদানই আমাদের লক্ষ।",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "১. সংগ্রহকৃত তথ্য:",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "আমরা আপনার কোনো ব্যক্তিগত তথ্য (যেমন নাম, ঠিকানা বা মোবাইল নম্বর) সরাসরি সংগ্রহ করি না। শুধুমাত্র অ্যাপ্লিকেশন ক্র্যাশ রিপোর্ট ও এনালিটিক্স সংক্রান্ত সাধারণ ডিভাইস প্যারামিটার তথ্য সংরক্ষণ করি যা অ্যাপ উন্নতিকরণে অবদান রাখে।",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "২. তথ্য শেয়ারিং:",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "আমরা সংগৃহীত কোনো ক্র্যাশ ডেটা বা পরিমিত ডেটা কোনো তৃতীয় পক্ষের সাথে শেয়ার করি না এবং এটি সম্পূর্ণরূপে সুরক্ষিত ও সুরক্ষিত প্রক্রিয়ায় পরিচালিত হয়।",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    } else {
        // Render simple Grid of options
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Branding Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDarkBg),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "খেলা দেখুন",
                        color = GreenPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "প্রিমিয়াম স্পোর্টস ও লাইভ আইপিটিভি",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Option Cards
            MenuOptionCard(
                title = "ভাঙা/অচল চ্যানেল রিপোর্ট (Report Broken Stream)",
                description = "সমস্যা থাকলে রিপোর্ট সাবমিট করুন",
                icon = Icons.Default.Report,
                color = RedAccent,
                onClick = { activeSubPage = SubPage.ReportBroken }
            )

            MenuOptionCard(
                title = "যোগাযোগ ও সাপোর্ট (Contact Support)",
                description = "গ্রাহক সহায়তা দলের সাথে কথা বলুন",
                icon = Icons.Default.HeadsetMic,
                color = GreenPrimary,
                onClick = { activeSubPage = SubPage.ContactSupport }
            )

            MenuOptionCard(
                title = "অ্যাপ্লিকেশন পরিচিতি (About Apps)",
                description = "খেলা দেখুন এর বর্ণনা ও চমৎকার ফিচার",
                icon = Icons.Default.Info,
                color = Color.Cyan,
                onClick = { activeSubPage = SubPage.AboutApp }
            )

            MenuOptionCard(
                title = "গোপনীয়তা নীতি (Privacy Policy)",
                description = "আমাদের ডেটা ও তথ্যের সুরক্ষা নীতি",
                icon = Icons.Default.Security,
                color = Color.Yellow,
                onClick = { activeSubPage = SubPage.PrivacyPolicy }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "সংস্করণ - ১.০.০ (Build v2026)",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun MenuOptionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardDarkBg),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

enum class SubPage(val title: String) {
    ReportBroken("চ্যানেল রিপোর্ট করুন"),
    ContactSupport("সাপোর্ট ও যোগাযোগ"),
    AboutApp("অ্যাপ্লিকেশন পরিচিতি"),
    PrivacyPolicy("গোপনীয়তা নীতি")
}
