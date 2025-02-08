# Advanced Word Counter

A feature-rich Java application for text analysis with a modern graphical user interface. This application provides detailed statistics about text content and supports multiple file formats.

## Features

### Real-time Text Analysis
- Word count
- Character count
- Sentence count
- Paragraph count
- Reading time estimation
- Speaking time estimation
- Average word length
- Words per sentence

### Advanced Analysis
- Unique word count
- Word frequency analysis
- Longest word detection
- Top 20 most used words

### File Operations
- Open text files
- Save text content
- Export statistics to TXT
- Export word frequency to CSV
- Support for multiple file formats:
  - TXT files
  - PDF files
  - DOC/DOCX files

### Modern UI Features
- Dark/Light theme toggle
- File drag and drop support
- File preview before opening
- Progress bar for large files
- Clean and intuitive interface

## Requirements
- Java 11 or higher
- Maven 3.6 or higher

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/word-counter.git
   cd word-counter
   ```
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn exec:java
   ```

## Usage

### Text Input Methods
- Type directly into the text area
- Paste text from clipboard
- Upload a file using the "Upload File" button
- Drag and drop a file into the window

### Analyzing Text
- Statistics update automatically as you type
- Click "Analyze Text" for a detailed analysis popup
- View word frequency analysis through the detailed view

### Exporting Data
- Use `File → Export Statistics to TXT` for basic stats
- Use `File → Export Analysis to CSV` for word frequency data

### Theme Toggle
- Click the moon/sun icon to switch between dark and light themes

## Dependencies
- Apache PDFBox (2.0.29) - PDF file support
- Apache POI (5.2.3) - Microsoft Word file support

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Screenshots


https://github.com/user-attachments/assets/a42c1095-0eb8-4fde-bc81-475b3c402d38


## Authors

- Omkar

## Acknowledgments

- Inspired by the need for a modern, feature-rich text analysis tool
- Thanks to the Apache Foundation for PDFBox and POI libraries


