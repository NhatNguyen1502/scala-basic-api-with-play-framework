package services

import jakarta.inject.Inject
import play.api.Configuration
import play.api.libs.mailer.{Email, MailerClient}

import javax.inject.Singleton

@Singleton
class MailerService @Inject() (
  mailerClient: MailerClient,
  config: Configuration
) {
  private val serverUrl = config.get[String]("server.url")

  def sendVerificationEmail(to: String, token: String): Unit = {
    val link = s"$serverUrl/auth/verify?token=$token"
    val email = Email(
      subject = "Please verify your email",
      from = "zznhatphilong@gmail.com",
      to = Seq(to),
      bodyText = Some(s"Click the following link to verify your email: $link"),
      bodyHtml =
        Some(s"""<a href="$link">Click here to verify your email</a>""")
    )
    mailerClient.send(email)
  }

}
