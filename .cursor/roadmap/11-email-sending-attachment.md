# Task 11: Email Sending with Excel Attachment

## Overview
Implement email sending functionality using Gmail API. Attach the generated Excel file and send to configured recipients.

## Requirements

### Email Sending Features

1. **Send Email**
   - Use Gmail API to send email
   - Attach Excel file
   - Use configured subject and body
   - Send to all configured recipients

2. **Email Content**
   - Subject: From email config (user-defined)
   - Body: From email config (user-defined)
   - Attachment: Generated Excel file
   - Format: HTML or plain text (prefer HTML for formatting)

3. **Recipients**
   - Send to all recipients in email config
   - BCC or TO? (TO is more transparent)
   - Handle multiple recipients

4. **Error Handling**
   - Network errors
   - Authentication errors
   - Invalid recipients
   - File attachment errors
   - Show user-friendly error messages

5. **Success Feedback**
   - Show success message
   - Log sent email info
   - Optionally track sent emails

## Implementation Steps

### Step 1: Create Email Service
- `EmailService.kt`
- Use Gmail API client
- Handle authentication
- Send email with attachment

### Step 2: Implement Gmail API Client Setup
- Initialize Gmail service
- Use stored OAuth tokens
- Handle token refresh
- Create Gmail API instance

### Step 3: Create Email Message
- Build MIME message
- Set subject and body
- Attach Excel file
- Set recipients

### Step 4: Send Email Function
- Use Gmail API send method
- Handle response
- Return success/error

### Step 5: Integrate with UI
- Add send button to calendar screen
- Add send button to historical month detail
- Show loading state
- Show success/error messages

### Step 6: Update ViewModels
- Add send email function
- Handle async operation
- Update UI state

## File Structure
```
app/src/main/java/com/example/kriptogan/
├── data/
│   ├── email/
│   │   └── EmailService.kt
│   └── auth/
│       └── GmailAuthService.kt (already exists)
```

## Gmail API Implementation

### Initialize Gmail Service
```kotlin
class EmailService(private val context: Context) {
    private val gmailService: Gmail
    
    init {
        val credential = getCredential() // From GmailAuthService
        gmailService = Gmail.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("WorkClockMailer")
         .build()
    }
}
```

### Create MIME Message
```kotlin
fun createMessage(
    to: List<String>,
    subject: String,
    body: String,
    attachmentFile: File
): Message {
    val message = MimeMessage()
    message.setFrom(InternetAddress(senderEmail))
    message.setRecipients(Message.RecipientType.TO, to.map { InternetAddress(it) }.toTypedArray())
    message.subject = subject
    
    // Create multipart message with attachment
    val multipart = MimeMultipart()
    
    // Body part
    val bodyPart = MimeBodyPart()
    bodyPart.setText(body, "utf-8", "html")
    multipart.addBodyPart(bodyPart)
    
    // Attachment part
    val attachmentPart = MimeBodyPart()
    attachmentPart.attachFile(attachmentFile)
    attachmentPart.fileName = attachmentFile.name
    multipart.addBodyPart(attachmentPart)
    
    message.setContent(multipart)
    
    // Encode to base64url
    val encodedMessage = Base64.encodeBase64URLSafeString(message.toString().toByteArray())
    return Message().apply {
        raw = encodedMessage
    }
}
```

### Send Email
```kotlin
suspend fun sendEmail(
    recipients: List<String>,
    subject: String,
    body: String,
    excelFile: File
): Result<Unit> {
    return try {
        val message = createMessage(recipients, subject, body, excelFile)
        gmailService.users().messages().send("me", message).execute()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

## Email Content Formatting

### HTML Body (Optional)
```kotlin
val htmlBody = """
    <html>
        <body>
            <p>Please find attached the work hours report for ${monthName}.</p>
            <p>Best regards</p>
        </body>
    </html>
""".trimIndent()
```

### Plain Text Body
```kotlin
val textBody = """
    Please find attached the work hours report for $monthName.
    
    Best regards
""".trimIndent()
```

## Integration Points

### Calendar Screen
- "Send Report" button
- Generate Excel for current month
- Send email
- Show feedback

### Historical Month Detail
- "Resend Email" button
- Generate Excel for that month
- Send email
- Show feedback

## Error Handling

### Common Errors
1. **Authentication Error**
   - Token expired: Refresh token
   - No token: Prompt to sign in
   - Invalid token: Re-authenticate

2. **Network Error**
   - No internet: Show message
   - Timeout: Retry option
   - Server error: Show error

3. **Recipient Error**
   - Invalid email: Skip or show error
   - No recipients: Show warning

4. **File Error**
   - File not found: Regenerate
   - File too large: Compress or error

## User Feedback

### Loading State
- Show progress indicator
- Disable send button
- Show "Sending..." message

### Success State
- Show success snackbar
- "Email sent successfully"
- Option to view in Gmail app

### Error State
- Show error snackbar
- Error message
- Retry option (if applicable)

## Dependencies
- Already have Gmail API from Task 3
- Need JavaMail API for MIME:
```kotlin
implementation("com.sun.mail:android-mail:1.6.7")
implementation("com.sun.mail:android-activation:1.6.7")
```

## Security Considerations
- Never log email content
- Secure token storage (already handled)
- Validate recipients
- Handle sensitive data properly

## Testing Considerations
- Test with valid recipients
- Test with invalid recipients
- Test with large Excel files
- Test network errors
- Test authentication errors
- Test with multiple recipients

## Permissions
- Already have internet permission
- No additional permissions needed

## File Cleanup
- Delete Excel file after sending? (optional)
- Or keep for user reference
- Or keep for X days then delete
