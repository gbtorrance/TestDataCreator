# TestDataCreator
TestDataCreator (TDC) is a tool designed to help non-technical users easily create complex XML files with nothing more than a basic understanding of Microsoft Excel.

## An origin story

TestDataCreator was originally created to support the testing of a State Revenue agency's "Modernized eFile" ("MeF") implementation. At the most basic level, MeF is a standard defining how electronically-filed tax returns should be submitted to the IRS or State Revenue agencies. It was created by the IRS to support the filing of Federal returns, and was later adopted by State Revenue agencies for the filing of State returns (http://www.statemef.com/). There are currently many implementations of the MeF standard in use by State Revenue agancies.

If you have ever filed a tax return using TurboTax, TaxAct, or any of the thousands of tax-preparer services that spring up like weeds during the first few months of every tax year, then your return was likely processed by an MeF system.

MeF tax returns are made up of one or more -- often extremely complex -- XML tax-return files. To properly test an MeF system, hundreds of these files may be needed. Someone with a technical background, and knowledge of XML formatting, could certainly create the files (given enough time). But technical resources are often limited, and often don't have an in-depth understanding of the business domain (in this case, taxation). It is no easy task to find someone able to manually create complex XML files *and* understand the deep magic of tax law. Do such creatures even exist?

TestDataCreator was created to meet this need. Most people with an in-depth knowledge of taxation likely do not know how to manually create XML files. But a significant percentage of the population knows how to use Microsoft Excel. And therein lies the key to solving the MeF testing problem. And, why stop at MeF? Almost any XML files can be created using a similar approach.

## A little disclaimer
Earlier versions of TestDataCreator are successfully being used by *two* State Revenue agencies for MeF testing. However, the open-source version available on GitHub is still an "work in progress". (Code from earlier versions is gradually being refactored and improved for the open-source version.) Though the open-source version is far from complete, it might be helpful to get a sense for where this project is headed. With that goal in mind, some of the samples below were generated using *earlier* implementations of the tool. Ultimately, though, the open-source version will support everything the earlier versions support, and more.

## I'm bored, details please
TestDataCreator is an XML-configuration-based tool that uses XML schemas (XSD) and Microsoft Excel to support the creation and validation of XML test files. It is written in Java, and makes use of various libraries, including Apache Xerces (for XML schema processing) and Apache POI (for Excel file processing).

## Hmmm, that doesn't help much. How would it be used?
TestDataCreator would typically be used as follows:

- Start with a set of XML schema files.
- Determine the 'root' element within the schemas for each type of XML document to be created.
- Configure a 'model' based on each 'root' element. This configuration may include, for example, the maximum number of each repeating element (for elements where 'maxOccurs' is greater than 1).
- Using TestDataCreator, generate a 'book' file (essentially an Excel Workbook) containing one or more 'pages' (a Worksheet within that Workbook). 
  - The left-most columns of each 'page' will contain a structured (tree) representation of the 'model' defined for that 'page'. 
- Once generated, a 'book' file can then be provided to non-technical users/testers as the starting point for their testing.
  - Users can then create 'test cases' by filling in values within columns on the 'pages' of the Excel 'book' file.
  - Each column on a 'page' represent a 'test document' (which will ultimately be generated as an XML file). Each 'test document' conforms to the 'model' defined for its particular 'page'. 
  - A 'test case' can be made up of one or more 'test documents' (XML files) conforming to one or more different 'models'. 
  - A 'book' file can contain one or more 'test cases'.
- Once a user has created a 'book' file containing 'test cases', they can schema validate the 'test cases' using TestDataCreator. 
  - If any errors are discovered during schema validation, these errors will be written to a 'Log' worksheet in the Excel 'book', along with hyperlinks to the approximate location of each error within each 'test document'. (This helps the users to easily locate and correct errors.)
- Once a user is comfortable that the 'test cases' in a 'book' are ready for testing, they can use TestDataCreator to generate the 'test case' XML files (which can then be processed into the system under test).
- Wash, rinse, repeat.

## I'm a visual learner. Show me
Sure, why not. 
![Screenshot1](https://cloud.githubusercontent.com/assets/16735709/13550065/a7dc1384-e2e2-11e5-8da1-f3cadc465251.JPG)
- What we have here is a sample of a TestDataCreator 'book' containing two 'pages' ('1040A' and 'Manifest') and a 'Log' Worksheet. 
- On the left (beginning at row 10) is the start of a Federal 1040A form, represented visually, with levels indented to show the heirarchy of XML elements.
- 'Test cases' are shown beginning in column Y. (In this example there are two test cases.)
- Additional information, such as form line number, data type, notes, and occurrence is shown beginning in column U. (To save space, the text in some of these cells is 'shrunk', but it is easily viewable by clicking on each cell.)
- The 'Occurs' column (X) represents the 'minOccurs' and 'maxOccurs' for each element. (For example, '0..1' indicates minOccurs=0, maxOccurs=1, and '3..n' indicates minOccurs=3, maxOccurs=unbounded.)
- Non-leaf/parent elements are shown in blue. Attributes are shown in green, prefixed with a '@'.

![Screenshot2](https://cloud.githubusercontent.com/assets/16735709/13550063/a7d7a074-e2e2-11e5-8eff-689bedfb44e8.JPG)
- Above is another section of the IRS 1040A 'page'. 
- Notice the '>' markers to the left of 'USAddress' and 'ForeignAddress'? These indicate the start of 'choice' within the XML. 
  - In this case, the choice (represented by '[CHOICE]') is defined as '1..1', so it is required that either 'USAddress' or 'ForeignAddress' be provided. For both 'test cases' shown, 'USAddress' is provided. 
  - Within 'USAddress', however, 'AddressLine2Txt' is optional ('0..1'), and has not been provided.

![Screenshot3](https://cloud.githubusercontent.com/assets/16735709/13550066/a7dc8d1e-e2e2-11e5-8d14-6c0435ea485d.JPG)
- Above is another section of the IRS 1040A 'page'.
- Notice the '1' and '2' on the left? The 'DependentDetail' element can be repeated. (It is marked as '0..100', meaning minOccurs=0, maxOccurs=100.) In the image one can see that it is repeated at least 3 times.
  - In 'test case' 1, two dependents are provided (Sally and Josiah). 
  - In 'test case' 2, only one is provided (Jackson).
- Notice, also, the 'choice' represented within each repeating group? Any level of nested choices and repeating element groups is supported.

![Screenshot3](https://cloud.githubusercontent.com/assets/16735709/13550064/a7d9d74a-e2e2-11e5-9c3f-b69cccca1ea9.JPG)
- Above is one final example from the IRS 1040A 'page'.
- Notice the '1  1' and '1  2' on the left? That shows that the 'F1099RStateLocalTaxGrp' element repeats (twice in the image), but that it is also part of a larger group of elements that also repeats. Any level of repeating element groups is supported.
