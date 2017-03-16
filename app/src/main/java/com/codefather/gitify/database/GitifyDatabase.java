package com.codefather.gitify.database;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = GitifyDatabase.NAME, version = GitifyDatabase.VERSION)
public class GitifyDatabase {

    static final String NAME = "GitifyDatabase"; // we will add the .db extension

    static final int VERSION = 1;
}