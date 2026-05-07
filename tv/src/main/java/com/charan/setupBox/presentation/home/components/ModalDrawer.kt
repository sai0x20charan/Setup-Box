package com.charan.setupBox.presentation.home.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.Text
import com.charan.shared.supabase.SupabaseUtils

@Composable
fun ModelDrawer(){
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
    ){


            LazyColumn {
                item {
                    Text(text = SupabaseUtils.getUserName().toString(), style = MaterialTheme.typography.displayMedium)
                }
                item {
                    ListItem(
                        selected = true, 
                        onClick = { Log.d("TAG", "ModelDrawer: logout") }, 
                        headlineContent = { 
                            Text(text = "Logout")
                        })


                }
            }

        }




}