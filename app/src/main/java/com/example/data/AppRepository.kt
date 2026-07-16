package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AppRepository(private val appDao: AppDao) {

    val allIssues: Flow<List<Issue>> = appDao.getAllIssues()
    val allContacts: Flow<List<Contact>> = appDao.getAllContacts()

    fun getIssuesByStatus(status: String): Flow<List<Issue>> {
        return appDao.getIssuesByStatus(status)
    }

    suspend fun insertIssue(issue: Issue): Long {
        return appDao.insertIssue(issue)
    }

    suspend fun insertContacts(contacts: List<Contact>) {
        appDao.insertContacts(contacts)
    }

    suspend fun updateIssue(issue: Issue) {
        appDao.updateIssue(issue)
    }

    suspend fun deleteIssue(issue: Issue) {
        appDao.deleteIssue(issue)
    }

    suspend fun seedDatabaseIfEmpty() {
        // Seed Contacts
        val currentContacts = allContacts.first()
        if (currentContacts.isEmpty()) {
            val defaultContacts = listOf(
                // Project Team Users
                Contact(name = "Moni shah", phone = "9322277387", company = "M/s Venus Electric Store", isPhoneContact = false),
                Contact(name = "Test agency contact", phone = "+919136033327", company = "Test Agency", isPhoneContact = false),
                Contact(name = "Vishal tatte", phone = "+919595530660", company = "Main Team", isPhoneContact = false),
                Contact(name = "Sanatan bahera", phone = "+917506426429", company = "Main Team", isPhoneContact = false),
                Contact(name = "Romit patel", phone = "+918879851812", company = "Main Team", isPhoneContact = false),
                Contact(name = "Utkarsha bhujbal", phone = "+919000000000", company = "Main Team", isPhoneContact = false),

                // Phone Contacts
                Contact(name = "# (Emergency)", phone = "7477730346", company = "Phone Contact", isPhoneContact = true),
                Contact(name = "Siddhmesh mane", phone = "+918369689052", company = "Phone Contact", isPhoneContact = true),
                Contact(name = "+918652790603", phone = "+918652790603", company = "Phone Contact", isPhoneContact = true),
                Contact(name = "+919327915124", phone = "+919327915124", company = "Phone Contact", isPhoneContact = true),
                Contact(name = "+919762821355", phone = "+919762821355", company = "Phone Contact", isPhoneContact = true),
                Contact(name = "097-020-46431", phone = "09702046431", company = "Phone Contact", isPhoneContact = true),
                Contact(name = "6471 Vilas", phone = "+918888951198", company = "Phone Contact", isPhoneContact = true),
                Contact(name = "91362 41810", phone = "9136241810", company = "Phone Contact", isPhoneContact = true),
                Contact(name = "A (Supervisor)", phone = "8779608616", company = "Phone Contact", isPhoneContact = true)
            )
            appDao.insertContacts(defaultContacts)
        }

        // Seed Issues
        val currentIssues = allIssues.first()
        if (currentIssues.isEmpty()) {
            val defaultIssues = mutableListOf<Issue>()
            
            // 1 Raised issue (from original)
            defaultIssues.add(
                Issue(
                    project = "Training Project",
                    locationPath = "TP / Techsec 602 / Conference Room",
                    status = "Raised",
                    description = "Ceiling lights flickering and Material mismatch found.",
                    deadline = "Wed, 08 July 01:03 PM",
                    category = "Material",
                    assignee = "Lakshya sarin",
                    reporter = "Deepesh dhavan"
                )
            )

            // 1 Redo issue
            defaultIssues.add(
                Issue(
                    project = "ANKUR",
                    locationPath = "ANK / Block B / Floor 3 / Room 304",
                    status = "Redo",
                    description = "Wall plastering uneven and cracked.",
                    deadline = "Fri, 10 July 04:00 PM",
                    category = "Quality",
                    assignee = "Moni shah",
                    reporter = "Saharsh Sathyanarayanan"
                )
            )

            // 14 Failed issues to match the "14" in the dashboard mock image
            for (i in 1..14) {
                defaultIssues.add(
                    Issue(
                        project = if (i % 2 == 0) "ANKUR" else "Training Project",
                        locationPath = if (i % 2 == 0) "ANK / Tower A / Flat ${100 + i}" else "TP / Wing 1 / Flat ${200 + i}",
                        status = "Failed",
                        description = "Concrete curing test #$i failed core strength test.",
                        deadline = "Thu, 09 July 12:00 PM",
                        category = "Quality",
                        assignee = "Vishal tatte",
                        reporter = "Saharsh Sathyanarayanan"
                    )
                )
            }

            for (issue in defaultIssues) {
                appDao.insertIssue(issue)
            }
        }
    }
}
