import com.microsoft.graphdataconnect.skillsfinder.EnronMimeToGdcTransformJob
import org.junit.{Assert, Test}

class RecipientsListParserTest {
  @Test
  def testComplexNames2DoubleQuotes(): Unit = {
    val xRecipientsField = "\"\"Aleck, Dadson (E-mail)\"\" <Aleck.Dadson@enron.com>, \n " +
      "\"\"Allen Langdon (E-mail)\"\" <IMCEAEX-_O=GPC_OU=VICTORIA_cn=Recipients_cn=ALangdon@GPC.CA>, " +
      "\"\"Eric Thode (E-mail)\"\" <Eric.Thode@enron.com>, " +
      "\"\"Zelikovitz, Evan\"\" <EZelikovitz@GPC.CA>, " +
      "\"\"Glenn Leslie (E-mail)\"\" <glenn.leslie@blakes.com>, " +
      "\"\"Peterson, Kevin\"\" <KPeterson@gpc.ca>, " +
      "\"\"Mark Palmer (E-mail)\"\" <mpalmer@enron.com>, " +
      "\"\"Reder, Mark\"\" <MReder@gpc.ca>, " +
      "\"\"Varley, Peter\"\" <PVarley@GPC.CA>, " +
      "\"\"Richard Shapiro (E-mail)\"\" <rshapiro@enron.com>, " +
      "\"\"Rob Milnthorp (E-mail)\"\" <Rob.Milnthorp@enron.com>, " +
      "\"\"Robert Hemstock (E-mail)\"\" <Robert.Hemstock@enron.com>, " +
      "\"\"Steven Kean (E-mail)\"\" <skean@enron.com>, " +
      "\"\"McLaren, Tom\"\" <TMcLaren@GPC.CA>, " +
      "\"\"Albush, Vern\"\" <VAlbush@GPC.CA>"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("\"\"Aleck, Dadson (E-mail)\"\" <Aleck.Dadson@enron.com>",
      "\"\"Allen Langdon (E-mail)\"\" <IMCEAEX-_O=GPC_OU=VICTORIA_cn=Recipients_cn=ALangdon@GPC.CA>",
      "\"\"Eric Thode (E-mail)\"\" <Eric.Thode@enron.com>",
      "\"\"Zelikovitz, Evan\"\" <EZelikovitz@GPC.CA>",
      "\"\"Glenn Leslie (E-mail)\"\" <glenn.leslie@blakes.com>",
      "\"\"Peterson, Kevin\"\" <KPeterson@gpc.ca>",
      "\"\"Mark Palmer (E-mail)\"\" <mpalmer@enron.com>",
      "\"\"Reder, Mark\"\" <MReder@gpc.ca>",
      "\"\"Varley, Peter\"\" <PVarley@GPC.CA>",
      "\"\"Richard Shapiro (E-mail)\"\" <rshapiro@enron.com>",
      "\"\"Rob Milnthorp (E-mail)\"\" <Rob.Milnthorp@enron.com>",
      "\"\"Robert Hemstock (E-mail)\"\" <Robert.Hemstock@enron.com>",
      "\"\"Steven Kean (E-mail)\"\" <skean@enron.com>",
      "\"\"McLaren, Tom\"\" <TMcLaren@GPC.CA>",
      "\"\"Albush, Vern\"\" <VAlbush@GPC.CA>")
    Assert.assertTrue(expected == parsedRecipients)
  }

  @Test
  def testComplexNamesMixedQuotes(): Unit = {
    val xRecipientsField = "\"\'Aleck, Dadson (E-mail)\'\" <Aleck.Dadson@enron.com>, " +
      "\"\'Allen Langdon (E-mail)\'\" <IMCEAEX-_O=GPC_OU=VICTORIA_cn=Recipients_cn=ALangdon@GPC.CA>, " +
      "\"\'Eric Thode (E-mail)\'\" <Eric.Thode@enron.com>, " +
      "\"\'Zelikovitz, Evan\'\" <EZelikovitz@GPC.CA>, " +
      "\"\'Glenn Leslie (E-mail)\'\" <glenn.leslie@blakes.com>, " +
      "\"\'Peterson, Kevin\'\" <KPeterson@gpc.ca>, " +
      "\"\'Mark Palmer (E-mail)\'\" <mpalmer@enron.com>, " +
      "\"\'Reder, Mark\'\" <MReder@gpc.ca>, " +
      "\"\'Varley, Peter\'\" <PVarley@GPC.CA>, " +
      "\"\'Richard Shapiro (E-mail)\'\" <rshapiro@enron.com>, " +
      "\"\'Rob Milnthorp (E-mail)\'\" <Rob.Milnthorp@enron.com>, " +
      "\"\'Robert Hemstock (E-mail)\'\" <Robert.Hemstock@enron.com>, " +
      "\"\'Steven Kean (E-mail)\'\" <skean@enron.com>, " +
      "\"\'McLaren, Tom\'\" <TMcLaren@GPC.CA>, " +
      "\"\'Albush, Vern\'\" <VAlbush@GPC.CA>"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("\"\'Aleck, Dadson (E-mail)\'\" <Aleck.Dadson@enron.com>",
      "\"\'Allen Langdon (E-mail)\'\" <IMCEAEX-_O=GPC_OU=VICTORIA_cn=Recipients_cn=ALangdon@GPC.CA>",
      "\"\'Eric Thode (E-mail)\'\" <Eric.Thode@enron.com>",
      "\"\'Zelikovitz, Evan\'\" <EZelikovitz@GPC.CA>",
      "\"\'Glenn Leslie (E-mail)\'\" <glenn.leslie@blakes.com>",
      "\"\'Peterson, Kevin\'\" <KPeterson@gpc.ca>",
      "\"\'Mark Palmer (E-mail)\'\" <mpalmer@enron.com>",
      "\"\'Reder, Mark\'\" <MReder@gpc.ca>",
      "\"\'Varley, Peter\'\" <PVarley@GPC.CA>",
      "\"\'Richard Shapiro (E-mail)\'\" <rshapiro@enron.com>",
      "\"\'Rob Milnthorp (E-mail)\'\" <Rob.Milnthorp@enron.com>",
      "\"\'Robert Hemstock (E-mail)\'\" <Robert.Hemstock@enron.com>",
      "\"\'Steven Kean (E-mail)\'\" <skean@enron.com>",
      "\"\'McLaren, Tom\'\" <TMcLaren@GPC.CA>",
      "\"\'Albush, Vern\'\" <VAlbush@GPC.CA>")
    Assert.assertTrue(expected == parsedRecipients)
  }

  @Test
  def testComplexNames1SingleQuotes(): Unit = {
    val xRecipientsField = "\'Aleck, Dadson (E-mail)\' <Aleck.Dadson@enron.com>, " +
      "\'Allen Langdon (E-mail)\' <IMCEAEX-_O=GPC_OU=VICTORIA_cn=Recipients_cn=ALangdon@GPC.CA>, " +
      "\'Eric Thode (E-mail)\' <Eric.Thode@enron.com>, " +
      "\'Zelikovitz, Evan\' <EZelikovitz@GPC.CA>, " +
      "\'Glenn Leslie (E-mail)\' <glenn.leslie@blakes.com>, " +
      "\'Peterson, Kevin\' <KPeterson@gpc.ca>, " +
      "\'Mark Palmer (E-mail)\' <mpalmer@enron.com>, " +
      "\'Reder, Mark\' <MReder@gpc.ca>, " +
      "\'Varley, Peter\' <PVarley@GPC.CA>, " +
      "\'Richard Shapiro (E-mail)\' <rshapiro@enron.com>, " +
      "\'Rob Milnthorp (E-mail)\' <Rob.Milnthorp@enron.com>, " +
      "\'Robert Hemstock (E-mail)\' <Robert.Hemstock@enron.com>, " +
      "\'Steven Kean (E-mail)\' <skean@enron.com>, " +
      "\'McLaren, Tom\' <TMcLaren@GPC.CA>, " +
      "\'Albush, Vern\' <VAlbush@GPC.CA>"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("\'Aleck, Dadson (E-mail)\' <Aleck.Dadson@enron.com>",
      "\'Allen Langdon (E-mail)\' <IMCEAEX-_O=GPC_OU=VICTORIA_cn=Recipients_cn=ALangdon@GPC.CA>",
      "\'Eric Thode (E-mail)\' <Eric.Thode@enron.com>",
      "\'Zelikovitz, Evan\' <EZelikovitz@GPC.CA>",
      "\'Glenn Leslie (E-mail)\' <glenn.leslie@blakes.com>",
      "\'Peterson, Kevin\' <KPeterson@gpc.ca>",
      "\'Mark Palmer (E-mail)\' <mpalmer@enron.com>",
      "\'Reder, Mark\' <MReder@gpc.ca>",
      "\'Varley, Peter\' <PVarley@GPC.CA>",
      "\'Richard Shapiro (E-mail)\' <rshapiro@enron.com>",
      "\'Rob Milnthorp (E-mail)\' <Rob.Milnthorp@enron.com>",
      "\'Robert Hemstock (E-mail)\' <Robert.Hemstock@enron.com>",
      "\'Steven Kean (E-mail)\' <skean@enron.com>",
      "\'McLaren, Tom\' <TMcLaren@GPC.CA>",
      "\'Albush, Vern\' <VAlbush@GPC.CA>")
    Assert.assertTrue(expected == parsedRecipients)
  }

  @Test
  def testComplexNames2SingleQuotes(): Unit = {
    val xRecipientsField = "\'\'Aleck, Dadson (E-mail)\'\' <Aleck.Dadson@enron.com>, " +
      "\'\'Allen Langdon (E-mail)\'\' <IMCEAEX-_O=GPC_OU=VICTORIA_cn=Recipients_cn=ALangdon@GPC.CA>, " +
      "\'\'Eric Thode (E-mail)\'\' <Eric.Thode@enron.com>, " +
      "\'\'Zelikovitz, Evan\'\' <EZelikovitz@GPC.CA>, " +
      "\'\'Glenn Leslie (E-mail)\'\' <glenn.leslie@blakes.com>, " +
      "\'\'Peterson, Kevin\'\' <KPeterson@gpc.ca>, " +
      "\'\'Mark Palmer (E-mail)\'\' <mpalmer@enron.com>, " +
      "\'\'Reder, Mark\'\' <MReder@gpc.ca>, " +
      "\'\'Varley, Peter\'\' <PVarley@GPC.CA>, " +
      "\'\'Richard Shapiro (E-mail)\'\' <rshapiro@enron.com>, " +
      "\'\'Rob Milnthorp (E-mail)\'\' <Rob.Milnthorp@enron.com>, " +
      "\'\'Robert Hemstock (E-mail)\'\' <Robert.Hemstock@enron.com>, " +
      "\'\'Steven Kean (E-mail)\'\' <skean@enron.com>, " +
      "\'\'McLaren, Tom\'\' <TMcLaren@GPC.CA>, " +
      "\'\'Albush, Vern\'\' <VAlbush@GPC.CA>"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("\'\'Aleck, Dadson (E-mail)\'\' <Aleck.Dadson@enron.com>",
      "\'\'Allen Langdon (E-mail)\'\' <IMCEAEX-_O=GPC_OU=VICTORIA_cn=Recipients_cn=ALangdon@GPC.CA>",
      "\'\'Eric Thode (E-mail)\'\' <Eric.Thode@enron.com>",
      "\'\'Zelikovitz, Evan\'\' <EZelikovitz@GPC.CA>",
      "\'\'Glenn Leslie (E-mail)\'\' <glenn.leslie@blakes.com>",
      "\'\'Peterson, Kevin\'\' <KPeterson@gpc.ca>",
      "\'\'Mark Palmer (E-mail)\'\' <mpalmer@enron.com>",
      "\'\'Reder, Mark\'\' <MReder@gpc.ca>",
      "\'\'Varley, Peter\'\' <PVarley@GPC.CA>",
      "\'\'Richard Shapiro (E-mail)\'\' <rshapiro@enron.com>",
      "\'\'Rob Milnthorp (E-mail)\'\' <Rob.Milnthorp@enron.com>",
      "\'\'Robert Hemstock (E-mail)\'\' <Robert.Hemstock@enron.com>",
      "\'\'Steven Kean (E-mail)\'\' <skean@enron.com>",
      "\'\'McLaren, Tom\'\' <TMcLaren@GPC.CA>",
      "\'\'Albush, Vern\'\' <VAlbush@GPC.CA>")
    Assert.assertTrue(expected == parsedRecipients)
  }


  @Test
  def testComplexNames1DoubleQuote(): Unit = {
    val xRecipientsField = "\"Aleck, Dadson (E-mail)\" <Aleck.Dadson@enron.com>, " +
      "\"Allen Langdon (E-mail)\" <IMCEAEX-_O=GPC_OU=VICTORIA_cn=Recipients_cn=ALangdon@GPC.CA>, " +
      "\"Eric Thode (E-mail)\" <Eric.Thode@enron.com>, " +
      "\"Zelikovitz, Evan\" <EZelikovitz@GPC.CA>, " +
      "\"Glenn Leslie (E-mail)\" <glenn.leslie@blakes.com>, " +
      "\"Peterson, Kevin\" <KPeterson@gpc.ca>, " +
      "\"Mark Palmer (E-mail)\" <mpalmer@enron.com>, " +
      "\"Reder, Mark\" <MReder@gpc.ca>, " +
      "\"Varley, Peter\" <PVarley@GPC.CA>, " +
      "\"Richard Shapiro (E-mail)\" <rshapiro@enron.com>, " +
      "\"Rob Milnthorp (E-mail)\" <Rob.Milnthorp@enron.com>, " +
      "\"Robert Hemstock (E-mail)\" <Robert.Hemstock@enron.com>, " +
      "\"Steven Kean (E-mail)\" <skean@enron.com>, " +
      "\"McLaren, Tom\" <TMcLaren@GPC.CA>, " +
      "\"Albush, Vern\" <VAlbush@GPC.CA>"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("\"Aleck, Dadson (E-mail)\" <Aleck.Dadson@enron.com>",
      "\"Allen Langdon (E-mail)\" <IMCEAEX-_O=GPC_OU=VICTORIA_cn=Recipients_cn=ALangdon@GPC.CA>",
      "\"Eric Thode (E-mail)\" <Eric.Thode@enron.com>",
      "\"Zelikovitz, Evan\" <EZelikovitz@GPC.CA>",
      "\"Glenn Leslie (E-mail)\" <glenn.leslie@blakes.com>",
      "\"Peterson, Kevin\" <KPeterson@gpc.ca>",
      "\"Mark Palmer (E-mail)\" <mpalmer@enron.com>",
      "\"Reder, Mark\" <MReder@gpc.ca>",
      "\"Varley, Peter\" <PVarley@GPC.CA>",
      "\"Richard Shapiro (E-mail)\" <rshapiro@enron.com>",
      "\"Rob Milnthorp (E-mail)\" <Rob.Milnthorp@enron.com>",
      "\"Robert Hemstock (E-mail)\" <Robert.Hemstock@enron.com>",
      "\"Steven Kean (E-mail)\" <skean@enron.com>",
      "\"McLaren, Tom\" <TMcLaren@GPC.CA>",
      "\"Albush, Vern\" <VAlbush@GPC.CA>")
    Assert.assertTrue(expected == parsedRecipients)
  }

  @Test
  def testEnronInternalNames(): Unit = {
    val xRecipientsField = "Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>, " +
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>, " +
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>, " +
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>, " +
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>, " +
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>, " +
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>, " +
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>",
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>",
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>",
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>",
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>",
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>",
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>",
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>")
    Assert.assertTrue(expected == parsedRecipients)
  }

  @Test
  def testEnronInternalNamesStartWith1Email(): Unit = {
    val xRecipientsField = "Beverly.Beaty@example.com, Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>, " +
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>, " +
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>, " +
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>, " +
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>, " +
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>, " +
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>, " +
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Beverly.Beaty@example.com", "Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>",
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>",
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>",
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>",
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>",
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>",
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>",
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>")
    Assert.assertTrue(expected == parsedRecipients)
  }

  @Test
  def testEnronInternalNamesStartWith2Emails(): Unit = {
    val xRecipientsField = "Beverly.Beaty@example.com, Dan.Junek@example.com, Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>, " +
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>, " +
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>, " +
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>, " +
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>, " +
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>, " +
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>, " +
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Beverly.Beaty@example.com", "Dan.Junek@example.com", "Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>",
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>",
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>",
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>",
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>",
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>",
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>",
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>")
    Assert.assertTrue(expected == parsedRecipients)
  }

  @Test
  def testEnronInternalNamesStartWith3Emails(): Unit = {
    val xRecipientsField = "Beverly.Beaty@example.com, Dan.Junek@example.com, David.Marye@example.com, Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>, " +
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>, " +
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>, " +
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>, " +
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>, " +
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>, " +
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>, " +
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Beverly.Beaty@example.com", "Dan.Junek@example.com", "David.Marye@example.com", "Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>",
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>",
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>",
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>",
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>",
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>",
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>",
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>")
    Assert.assertTrue(expected == parsedRecipients)
  }


  @Test
  def testEnronInternalNames1EmailAfterOddCount(): Unit = {
    val xRecipientsField = "Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>, " +
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>, " +
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>, " +
      "Beverly.Beaty@example.com, " +
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>, " +
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>, " +
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>, " +
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>, " +
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>",
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>",
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>",
      "Beverly.Beaty@example.com",
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>",
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>",
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>",
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>",
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>")
    Assert.assertTrue(expected == parsedRecipients)
  }

  @Test
  def testEnronInternalNames2EmailsAfterOddCount(): Unit = {
    val xRecipientsField = "Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>, " +
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>, " +
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>, " +
      "Beverly.Beaty@example.com, Dan.Junek@example.com, " +
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>, " +
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>, " +
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>, " +
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>, " +
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>",
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>",
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>",
      "Beverly.Beaty@example.com", "Dan.Junek@example.com",
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>",
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>",
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>",
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>",
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>")
    Assert.assertTrue(expected == parsedRecipients)
  }

  @Test
  def testEnronInternalNames3EmailsAfterOddCount(): Unit = {
    val xRecipientsField = "Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>, " +
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>, " +
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>, " +
      "Beverly.Beaty@example.com, Dan.Junek@example.com, David.Marye@example.com, " +
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>, " +
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>, " +
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>, " +
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>, " +
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>",
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>",
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>",
      "Beverly.Beaty@example.com", "Dan.Junek@example.com", "David.Marye@example.com",
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>",
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>",
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>",
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>",
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>")
    Assert.assertTrue(expected == parsedRecipients)
  }


  @Test
  def testEnronInternalNames1EmailAfterEvenCount(): Unit = {
    val xRecipientsField = "Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>, " +
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>, " +
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>, " +
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>, " +
      "Beverly.Beaty@example.com, " +
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>, " +
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>, " +
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>, " +
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>",
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>",
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>",
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>",
      "Beverly.Beaty@example.com",
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>",
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>",
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>",
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>")
    Assert.assertTrue(expected == parsedRecipients)
  }

  @Test
  def testEnronInternalNames2EmailsAfterEvenCount(): Unit = {
    val xRecipientsField = "Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>, " +
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>, " +
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>, " +
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>, " +
      "Beverly.Beaty@example.com, Dan.Junek@example.com, " +
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>, " +
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>, " +
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>, " +
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>",
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>",
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>",
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>",
      "Beverly.Beaty@example.com", "Dan.Junek@example.com",
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>",
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>",
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>",
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>")
    Assert.assertTrue(expected == parsedRecipients)
  }

  @Test
  def testEnronInternalNames3EmailsAfterEvenCount(): Unit = {
    val xRecipientsField = "Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>, " +
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>, " +
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>, " +
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>, " +
      "Beverly.Beaty@example.com, Dan.Junek@example.com, David.Marye@example.com, " +
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>, " +
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>, " +
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>, " +
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Villarreal, Alexandra </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Avillar4>",
      "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>",
      "Bates, Kimberly </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kbates>",
      "Presas, Jessica </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jpresas>",
      "Beverly.Beaty@example.com", "Dan.Junek@example.com", "David.Marye@example.com",
      "Vuittonet, Laura </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lvuitton>",
      "Young, Becky </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Byoung>",
      "Huble, Amanda </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ahuble>",
      "Salinas, Michael </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Msalina2>")
    Assert.assertTrue(expected == parsedRecipients)
  }


  @Test
  def testFullNames(): Unit = {
    val xRecipientsField = "Scott Hendrickson, Victor Lamadrid, Dick Jenkins, Beverly Beaty, Dan Junek, " +
      "David Marye, Scott Loving, Laura Harder"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Scott Hendrickson", "Victor Lamadrid", "Dick Jenkins", "Beverly Beaty", "Dan Junek",
      "David Marye", "Scott Loving", "Laura Harder")
    Assert.assertTrue(expected == parsedRecipients)
  }

  @Test
  def testFullNames1EmailAfterOddCount(): Unit = {
    val xRecipientsField = "Scott Hendrickson, Victor Lamadrid, Dick Jenkins, Beverly.Beaty@example.com, Dan Junek, " +
      "David Marye, Scott Loving, Laura Harder"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Scott Hendrickson", "Victor Lamadrid", "Dick Jenkins", "Beverly.Beaty@example.com", "Dan Junek",
      "David Marye", "Scott Loving", "Laura Harder")
    Assert.assertTrue(expected == parsedRecipients)
  }

  def testFullNames2EmailsAfterOddCount(): Unit = {
    val xRecipientsField = "Scott Hendrickson, Victor Lamadrid, Dick Jenkins, Beverly.Beaty@example.com, Dan.Junek@example.com, " +
      "David Marye, Scott Loving, Laura Harder"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Scott Hendrickson", "Victor Lamadrid", "Dick Jenkins", "Beverly.Beaty@example.com", "Dan.Junek@example.com",
      "David Marye", "Scott Loving", "Laura Harder")
    Assert.assertTrue(expected == parsedRecipients)
  }

  def testFullNames3EmailsAfterOddCount(): Unit = {
    val xRecipientsField = "Scott Hendrickson, Victor Lamadrid, Dick Jenkins, Beverly.Beaty@example.com, Dan.Junek@example.com, " +
      "David.Marye@example.com, Scott Loving, Laura Harder"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Scott Hendrickson", "Victor Lamadrid", "Dick Jenkins", "Beverly.Beaty@example.com", "Dan.Junek@example.com",
      "David.Marye@example.com", "Scott Loving", "Laura Harder")
    Assert.assertTrue(expected == parsedRecipients)
  }


  @Test
  def testFullNames1EmailAfterEvenCount(): Unit = {
    val xRecipientsField = "Scott Hendrickson, Victor Lamadrid, Beverly.Beaty@example.com, Dan Junek, " +
      "David Marye, Scott Loving, Laura Harder"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Scott Hendrickson", "Victor Lamadrid", "Beverly.Beaty@example.com", "Dan Junek",
      "David Marye", "Scott Loving", "Laura Harder")
    Assert.assertTrue(expected == parsedRecipients)
  }

  def testFullNames2EmailsAfterEvenCount(): Unit = {
    val xRecipientsField = "Scott Hendrickson, Victor Lamadrid, Beverly.Beaty@example.com, Dan.Junek@example.com, " +
      "David Marye, Scott Loving, Laura Harder"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Scott Hendrickson", "Victor Lamadrid", "Beverly.Beaty@example.com", "Dan.Juneky@example.com",
      "David Marye", "Scott Loving", "Laura Harder")
    Assert.assertTrue(expected == parsedRecipients)
  }

  def testFullNames3EmailsAfterEvenCount(): Unit = {
    val xRecipientsField = "Scott Hendrickson, Victor Lamadrid, Beverly.Beaty@example.com, Dan.Junek@example.com, " +
      "David.Marye@example.com, Scott Loving, Laura Harder"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Scott Hendrickson", "Victor Lamadrid", "Beverly.Beaty@example.com", "Dan.Juneky@example.com",
      "David.Marye@example.com", "Scott Loving", "Laura Harder")
    Assert.assertTrue(expected == parsedRecipients)
  }

  @Test
  def testFullNamesStartWith1Email(): Unit = {
    val xRecipientsField = "Beverly.Beaty@example.com, Dan Junek, " +
      "David Marye, Scott Loving, Laura Harder"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Beverly.Beaty@example.com", "Dan Junek",
      "David Marye", "Scott Loving", "Laura Harder")
    Assert.assertTrue(expected == parsedRecipients)
  }

  def testFullNamesStartWith2Emails(): Unit = {
    val xRecipientsField = "Beverly.Beaty@example.com, Dan.Junek@example.com, " +
      "David Marye, Scott Loving, Laura Harder"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Beverly.Beaty@example.com", "Dan.Juneky@example.com",
      "David Marye", "Scott Loving", "Laura Harder")
    Assert.assertTrue(expected == parsedRecipients)
  }

  def testFullNamesStartWith3Emails(): Unit = {
    val xRecipientsField = "Beverly.Beaty@example.com, Dan.Junek@example.com, " +
      "David.Marye@example.com, Scott Loving, Laura Harder"
    val parsedRecipients = EnronMimeToGdcTransformJob.processNamesAndEmailsMixedListField(xRecipientsField)
    println(parsedRecipients.mkString(";;"))
    val expected = List("Beverly.Beaty@example.com", "Dan.Juneky@example.com",
      "David.Marye@example.com", "Scott Loving", "Laura Harder")
    Assert.assertTrue(expected == parsedRecipients)
  }
}
