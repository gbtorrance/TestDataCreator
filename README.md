# TestDataCreator

 [![Build Status](https://travis-ci.org/gbtorrance/TestDataCreator.svg?branch=master)](https://travis-ci.org/gbtorrance/TestDataCreator)  [![MIT licensed](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/gbtorrance/TestDataCreator/master/LICENSE)

*TestDataCreator* (*TDC*) helps **non-technical users** easily create and validate **complex XML documents** with nothing more than a basic understanding of **Microsoft Excel**. It is ideal for testing systems that require XML input files, but can be used in almost any context where XML Schemas are available and XML documents need to be created.

![screenshot1](https://cloud.githubusercontent.com/assets/16735709/13554422/742b89f0-e375-11e5-9f1a-1164e6ebb730.JPG)

### How might it be used?

A basic workflow with TestDataCreator consists of the following:

1. Initial configuration/setup. (Requires some basic technical ability.)
2. *TestDataCreator* reads an XML Schema and generates an Excel spreadsheet (*Book*) containing a structural representation of the Schema.
3. Non-technical users then enter any number of *Test Cases* in the *Book* file by filling in test values in columns. (Requires only basic Excel ability.)
4. *TestDataCreator* then processes the *Book* file, Schema-validates the *Test Cases*, and then generates XML documents for those *Test Cases*.
5. Additional *Tasks* can be performed on *Test Cases* with custom Java plugins (e.g. processing in a rules engine, exporting to an external system, etc.)

### Features

- Uses XML Schemas for *Book* file creation and *Test Case* validation.
- Leverages Microsoft Excel for easy *Test Case* creation by end users.
- Configuration is XML based, and highly customizable.
- *Book* files are configurable and can contain additional Element-level information to help users with *Test Case* creation. (Examples include Element datatype, document line references, field descriptions, and notes.) This approach enables non-technical users to not only understand the overall structure of the XML, but also the type and format of individual fields -- all without having to reference complicated XML Schemas.
- Supports optional *Customizer* spreadsheets during the configuration phase. This allow for very detailed customization of the information available to end-users in the *Book* file.
- Datatype information and Schema annotations can be automatically extracted from Schemas and made available to users in *Book* files.
- *Book* files can be automatically converted from one Schema version to another (retaining *Test Case* information so that it does not need to be re-entered when Schemas change). New lines -- resulting from Elements added to the Schema -- can be automatically highlight in converted *Book* files to give users a quick visual reference for changes.
- *Customizer* files can be automatically converted from one Schema version to another (retaining *Book* customizations so that they do not need to be re-entered when Schemas change). Changes in converted *Customizer* files can be highlighted (through custom fonts and colors), to make the process of adding new Schema versions to *TestDataCreator* configuration quick and painless.
- Custom *Tasks* plugins can be created (in Java) to perform post-processing on *Test Cases* (e.g. processing in a rules engine, exporting to an external system, etc.)
- Though *TestDataCreator*  can be run as a stand-alone tool, client-server extensions are being developed to more effectively support centralized administration and processing on more-capable server hardware. (This is ideal for situations where Schemas and *Book* files are large and complex, and user PCs are relatively underpowered.)

### Examples

If you're interested in playing around with a sample Excel *Book* file, click [here](https://github.com/gbtorrance/TestDataCreator/files/160333/2015_IRS-1040_Demo.xlsx).

For more information, please refer to the [Wiki](https://github.com/gbtorrance/TestDataCreator/wiki).

### Setup & Configuration

Details pending...

### Feedback & Support

Please send questions, concerns, and stock tips to greg.torrance@pobox.com.

### Licensing

*TestDataCreator* is an open source tool available under the [MIT License](https://github.com/gbtorrance/TestDataCreator/blob/master/LICENSE).