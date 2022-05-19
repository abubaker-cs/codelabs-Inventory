/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.inventory

import android.app.Application
import com.example.inventory.data.ItemRoomDatabase

// We will instantiate the database instance in the Application class
class BaseApplication : Application() {

    // Use lazy delegate so the instance database is lazily created when you first need/access the
    // reference (rather than when the app starts).
    val database: ItemRoomDatabase by lazy {

        //  Instantiate the database instance by calling getDatabase() on ItemRoomDatabase passing in the context
        ItemRoomDatabase.getDatabase(this)

    }

}

//  This will create the database (the physical database on the disk) on the first access.
