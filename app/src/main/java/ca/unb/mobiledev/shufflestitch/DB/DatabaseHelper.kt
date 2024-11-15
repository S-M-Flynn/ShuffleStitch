package ca.unb.mobiledev.shufflestitch.DB

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import ca.unb.mobiledev.shufflestitch.Item

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "MyDatabase.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "my_table"
        const val ITEM_ID = "ID"
        const val ITEM_PATH = "PATH"
        const val TOPS = "TOPS"
        const val BOTTOMS = "BOTTOMS"
        const val FULL_BODY = "FULL_BODY"
        const val SHOES = "SHOES"
        const val CASUAL = "CASUAL"
        const val PROFESSIONAL = "PROFESSIONAL"
        const val FORMAL = "FORMAL"
        const val ATHLETIC = "ATHLETIC"
        const val WINTER = "WINTER"
        const val FALL = "FALL"
        const val SPRING = "SPRING"
        const val SUMMER = "SUMMER"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT, 
                $ITEM_PATH TEXT, 
                $TOPS INTEGER, 
                $BOTTOMS INTEGER,
                $FULL_BODY INTEGER,
                $SHOES INTEGER,
                $CASUAL INTEGER,
                $PROFESSIONAL INTERGER,
                $FORMAL INTEGER,
                $ATHLETIC INTEGER,
                $WINTER INTEGER,
                $FALL INTEGER,
                $SPRING INTEGER,
                $SUMMER INTEGER
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insert a new record
    fun insertData(path: String, tags: IntArray): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(ITEM_PATH, path)
            put(TOPS, tags[0])
            put(BOTTOMS, tags[1])
            put(FULL_BODY, tags[2])
            put(SHOES, tags[3])
            put(CASUAL, tags[4])
            put(PROFESSIONAL, tags[5])
            put(FORMAL, tags[6])
            put(ATHLETIC, tags[7])
            put(WINTER, tags[8])
            put(FALL, tags[9])
            put(SPRING, tags[10])
            put(SUMMER, tags[11])
        }
        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return result != -1L // Returns true if insert is successful
    }

    fun getItemByPath(path: String): Item? {
        val db = this.readableDatabase
        val cursor: Cursor? = db.query(
            TABLE_NAME,
            null,
            "$ITEM_PATH = ?",
            arrayOf(path),
            null,
            null,
            null
        )

        var item: Item? = null
        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(ITEM_ID))
            val tops = cursor.getInt(cursor.getColumnIndexOrThrow(TOPS)) == 1
            val bottoms = cursor.getInt(cursor.getColumnIndexOrThrow(BOTTOMS)) == 1
            val fullBody = cursor.getInt(cursor.getColumnIndexOrThrow(FULL_BODY)) == 1
            val shoes = cursor.getInt(cursor.getColumnIndexOrThrow(SHOES)) == 1
            val casual = cursor.getInt(cursor.getColumnIndexOrThrow(CASUAL)) == 1
            val professional = cursor.getInt(cursor.getColumnIndexOrThrow(PROFESSIONAL)) == 1
            val formal = cursor.getInt(cursor.getColumnIndexOrThrow(FORMAL)) == 1
            val athletic = cursor.getInt(cursor.getColumnIndexOrThrow(ATHLETIC)) == 1
            val winter = cursor.getInt(cursor.getColumnIndexOrThrow(WINTER)) == 1
            val fall =  cursor.getInt(cursor.getColumnIndexOrThrow(FALL)) == 1
            val spring = cursor.getInt(cursor.getColumnIndexOrThrow(SPRING)) == 1
            val summer = cursor.getInt(cursor.getColumnIndexOrThrow(SUMMER)) == 1

            // Create the Item object
            item = Item(id, path, tops, bottoms, fullBody, shoes, casual, professional, formal, athletic, winter, fall, spring, summer)
        }

        cursor?.close()
        db.close()
        return item // Returns null if no match is found
    }

    fun updateItemByPath(path: String, updatedTags: IntArray): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(TOPS, updatedTags[0])
            put(BOTTOMS, updatedTags[1])
            put(FULL_BODY, updatedTags[2])
            put(SHOES, updatedTags[3])
            put(CASUAL, updatedTags[4])
            put(PROFESSIONAL, updatedTags[5])
            put(FORMAL, updatedTags[6])
            put(ATHLETIC, updatedTags[7])
            put(WINTER, updatedTags[8])
            put(FALL, updatedTags[9])
            put(SPRING, updatedTags[10])
            put(SUMMER, updatedTags[11])
        }

        val rowsUpdated = db.update(
            TABLE_NAME, // Table name
            contentValues, // Updated values
            "$ITEM_PATH = ?", // WHERE clause
            arrayOf(path) // WHERE arguments
        )

        db.close()
        return rowsUpdated > 0 // Returns true if at least one row was updated
    }


    // Retrieve all records
    fun getAllData(conditions: Map<String, String>): Map<String, List<Item>> {
        val itemList = mutableListOf<Item>()
        val db = this.readableDatabase

        // Build the WHERE clause and arguments dynamically
        val selection = conditions.keys.joinToString(" AND ") { "$it = ?" }
        val selectionArgs = conditions.values.toTypedArray()

        val cursor: Cursor = db.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(ITEM_ID))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_PATH))
                val tops = cursor.getInt(cursor.getColumnIndexOrThrow(TOPS)) == 1
                val bottoms = cursor.getInt(cursor.getColumnIndexOrThrow(BOTTOMS)) == 1
                val fullBody = cursor.getInt(cursor.getColumnIndexOrThrow(FULL_BODY)) == 1
                val shoes = cursor.getInt(cursor.getColumnIndexOrThrow(SHOES)) == 1
                val casual = cursor.getInt(cursor.getColumnIndexOrThrow(CASUAL)) == 1
                val professional = cursor.getInt(cursor.getColumnIndexOrThrow(PROFESSIONAL)) == 1
                val formal = cursor.getInt(cursor.getColumnIndexOrThrow(FORMAL)) == 1
                val athletic = cursor.getInt(cursor.getColumnIndexOrThrow(ATHLETIC)) == 1
                val winter = cursor.getInt(cursor.getColumnIndexOrThrow(WINTER)) == 1
                val fall =  cursor.getInt(cursor.getColumnIndexOrThrow(FALL)) == 1
                val spring = cursor.getInt(cursor.getColumnIndexOrThrow(SPRING)) == 1
                val summer = cursor.getInt(cursor.getColumnIndexOrThrow(SUMMER)) == 1

                itemList.add(Item(id, path, tops, bottoms, fullBody, shoes, casual, professional, formal, athletic, winter, fall, spring, summer))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        val topsList = itemList.filter { it.tops }
        val bottomsList = itemList.filter { it.bottoms }
        val fullBodyList = itemList.filter { it.fullBody }
        val shoesList = itemList.filter { it.shoes }

        // Return a map of the lists for easy access
        return mapOf(
            "tops" to topsList,
            "bottoms" to bottomsList,
            "fullBody" to fullBodyList,
            "shoes" to shoesList
        )
    }
}
