package com.example.data.repository

import com.example.data.api.BackendNote
import com.example.data.model.NotePage
import com.example.data.model.PdfNote

object PdfNotesRepository {
    val notes = listOf(
        // ==========================================
        // A LEVEL CS 9618 NOTES
        // ==========================================
        PdfNote(
            id = "alevel_p1_data_rep",
            title = "A-Level P1: Data Representation & Information",
            description = "Detailed study guide on Binary, Hexadecimal, Two's Complement representation, Audio digitization (sampling rate, bit depth), and Vector/Raster graphics.",
            category = "A Level (9618)",
            pages = 4,
            pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            youtubeUrl = "https://www.youtube.com/results?search_query=cie+a+level+cs+9618+data+representation",
            duration = "15:40",
            isPremium = false,
            contentPages = listOf(
                NotePage(
                    pageNumber = 1,
                    title = "Page 1: Number Systems & Hexadecimal",
                    body = "Hexadecimal is used as a user-friendly representation of binary values (e.g., in MAC addresses, IPv6, and HTML color codes). Conversion is simple: group binary bits in 4s (nybbles) and convert each to a hex digit (0-F).\n\nTwo's complement is used to represent signed integers. The most significant bit (MSB) carries a negative weight. To negate a number, invert all bits and add 1.",
                    codeSnippet = "# Python study check for Two's Complement conversion\ndef to_binary_8bit(val):\n    if val < 0:\n        val = (1 << 8) + val\n    return f\"{val:08b}\"\n\nprint(\"-5 in 8-bit Two's Complement:\", to_binary_8bit(-5))\n# Output: 11111011"
                ),
                NotePage(
                    pageNumber = 2,
                    title = "Page 2: Sound and Sampling",
                    body = "Sound is analogue and must be sampled to be stored digitally. Two main factors determine quality and file size:\n1. Sampling Rate: Number of sound samples taken per second (Hz).\n2. Sampling Resolution (Bit Depth): Number of bits used to encode each sample.\n\nNyquist's Theorem states that to capture a sound wave accurately, the sampling rate must be at least twice the highest frequency component of the sound.",
                    codeSnippet = "# Calculating sound file size\ndef calc_sound_size_bytes(duration_sec, sample_rate_hz, bit_depth, channels=2):\n    total_bits = duration_sec * sample_rate_hz * bit_depth * channels\n    return total_bits / 8\n\nsize = calc_sound_size_bytes(60, 44100, 16)\nprint(f\"1 minute CD-quality audio size: {size / (1024*1024):.2f} MB\")"
                ),
                NotePage(
                    pageNumber = 3,
                    title = "Page 3: Raster and Vector Images",
                    body = "Raster images are made of a grid of pixels. Each pixel has a binary value representing its color. \n- Resolution: Total pixels (width x height).\n- Color Depth: Number of bits per pixel.\nFile size = Width * Height * Color Depth.\n\nVector images use geometric equations (lines, circles, bezier curves) to draw the image. They scale infinitely without pixelation (resolution independent) and have smaller file sizes, but cannot represent realistic photographic scenes.",
                    codeSnippet = "<!-- Minimal SVG vector graphic example -->\n<svg height=\"100\" width=\"100\">\n  <circle cx=\"50\" cy=\"50\" r=\"40\" stroke=\"green\" stroke-width=\"3\" fill=\"white\" />\n</svg>"
                ),
                NotePage(
                    pageNumber = 4,
                    title = "Page 4: Exam Practice Questions",
                    body = "1. Show the step-by-step conversion of the denary number -42 into 8-bit Two's complement binary.\n2. Explain why increasing the sampling rate improves the accuracy of a recorded sound, and identify one drawback.\n3. Contrast raster and vector image storage. State which format is preferred for logos, and justify your choice.",
                    codeSnippet = null
                )
            )
        ),
        PdfNote(
            id = "alevel_p2_programming",
            title = "A-Level P2: Algorithms & Programming",
            description = "Master CIE pseudocode conventions, selection (IF, CASE), iteration (FOR, WHILE, REPEAT), 1D/2D arrays, and file reading/writing operations.",
            category = "A Level (9618)",
            pages = 3,
            pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            youtubeUrl = "https://www.youtube.com/results?search_query=cie+a+level+cs+9618+pseudocode",
            duration = "22:15",
            isPremium = false,
            contentPages = listOf(
                NotePage(
                    pageNumber = 1,
                    title = "Page 1: CIE Pseudocode Conventions",
                    body = "CIE Computer Science 9618 uses strict pseudocode syntax for exams. Key structures:\n- Assignment: `MyVariable <- Value`\n- Declarations: `DECLARE Name : STRING` or `DECLARE Grid : ARRAY[1:10, 1:10] OF INTEGER`\n- Conditional: `IF-THEN-ELSE-ENDIF` or `CASE OF MyVar ... ENDCASE`",
                    codeSnippet = "// Formal CIE 9618 Pseudocode\nDECLARE Scores : ARRAY[1:5] OF INTEGER\nDECLARE Index : INTEGER\nDECLARE HighScoreCount : INTEGER\n\nHighScoreCount <- 0\nFOR Index <- 1 TO 5\n    OUTPUT \"Enter score \", Index\n    INPUT Scores[Index]\n    IF Scores[Index] >= 80 THEN\n        HighScoreCount <- HighScoreCount + 1\n    ENDIF\nENDFOR\nOUTPUT \"Number of high scores: \", HighScoreCount"
                ),
                NotePage(
                    pageNumber = 2,
                    title = "Page 2: Array Searching & Sorting",
                    body = "Linear Search compares each array element sequentially until a match is found. Bubble Sort sweeps through the array, swapping adjacent elements if they are out of order, repeating until no swaps are made.\n\nLet's analyze the Bubble Sort algorithm in pseudocode. It uses nested loops and a boolean swap flag for efficiency.",
                    codeSnippet = "// Bubble Sort Pseudocode\nDECLARE Temp, Inner, Outer : INTEGER\nDECLARE Swapped : BOOLEAN\n\nREPEAT\n    Swapped <- FALSE\n    FOR Inner <- 1 TO (Length - 1)\n        IF List[Inner] > List[Inner + 1] THEN\n            Temp <- List[Inner]\n            List[Inner] <- List[Inner + 1]\n            List[Inner + 1] <- Temp\n            Swapped <- TRUE\n        ENDIF\n    ENDFOR\nUNTIL Swapped = FALSE"
                ),
                NotePage(
                    pageNumber = 3,
                    title = "Page 3: File Handling in Pseudocode",
                    body = "To store data persistently, programs read from and write to text files. \n- Open modes: READ, WRITE, APPEND.\n- Always CLOSE files after use to free up OS buffers.\n- Use EOF() (End Of File) to read until file ends.",
                    codeSnippet = "// Reading all lines from a student record file\nDECLARE FileLine : STRING\nOPENFILE \"StudentScores.txt\" FOR READ\nWHILE NOT EOF(\"StudentScores.txt\")\n    READFILE \"StudentScores.txt\", FileLine\n    OUTPUT \"Loaded line: \", FileLine\nENDWHILE\nCLOSEFILE \"StudentScores.txt\""
                )
            )
        ),
        PdfNote(
            id = "alevel_p3_networking",
            title = "A-Level P3: Advanced Encryption & Security",
            description = "Advanced Paper 3 guide on Asymmetric Cryptography, Digital Signatures, TLS/SSL handshakes, and Public Key Infrastructure.",
            category = "A Level (9618)",
            pages = 3,
            pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            youtubeUrl = "https://www.youtube.com/results?search_query=cie+a+level+cs+9618+asymmetric+encryption",
            duration = "18:20",
            isPremium = false,
            contentPages = listOf(
                NotePage(
                    pageNumber = 1,
                    title = "Page 1: Symmetric vs Asymmetric Encryption",
                    body = "Symmetric Encryption uses the same single key to both encrypt and decrypt data. Key distribution is highly risky.\n\nAsymmetric Encryption uses a mathematically linked key pair: a Public Key (freely distributed) and a Private Key (kept strictly confidential). If data is encrypted with the Public Key, ONLY the linked Private Key can decrypt it.",
                    codeSnippet = "# Python study tool illustrating key generation (Asymmetric analogy)\nimport random\n# Simple RSA-like modular math analogy\np, q = 61, 53 # Secret prime numbers\nn = p * q     # Public modulus\nprint(f\"Public Modulus n = {n} (This is shared publically)\")"
                ),
                NotePage(
                    pageNumber = 2,
                    title = "Page 2: Digital Signatures",
                    body = "Digital Signatures ensure non-repudiation and integrity. Process of signing:\n1. The sender hashes the message to get a message digest.\n2. The sender encrypts the digest with their own Private Key to create the Digital Signature.\n3. The receiver decrypts the signature using the sender's Public Key, gets the hash, and compares it to a newly generated hash of the message.",
                    codeSnippet = "// Digital Signature Flow Sequence\n// 1. Message -> MD5/SHA256 -> Hash Digest\n// 2. Hash Digest + Sender's Private Key -> Digital Signature\n// 3. Sent: [Message + Digital Signature]\n// 4. Verification: Decrypt Signature with Sender's Public Key -> Compare Hash"
                ),
                NotePage(
                    pageNumber = 3,
                    title = "Page 3: TLS/SSL Handshake & Certificates",
                    body = "Transport Layer Security (TLS) secures internet browsing. A Digital Certificate binds a public key to an entity's identity, signed by a trusted Certificate Authority (CA).\n\nThe handshake stages:\n1. Client connects, requests secure session.\n2. Server sends its Digital Certificate containing its public key.\n3. Client verifies certificate with built-in CA root certificates.\n4. Client generates a symmetric 'session key', encrypts it with server's public key, and sends it back.\n5. Server decrypts with private key; both use symmetric session key for fast, secure data transmission.",
                    codeSnippet = null
                )
            )
        ),
        PdfNote(
            id = "alevel_p4_adts",
            title = "A-Level P4: Practical OOP & Abstract Data Types",
            description = "Crucial Paper 4 programming reference containing Object Oriented Programming structures, Linked Lists, Stacks, Queues, and Binary Tree traversals.",
            category = "A Level (9618)",
            pages = 3,
            pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            youtubeUrl = "https://www.youtube.com/results?search_query=cie+a+level+cs+9618+paper+4+oop+python",
            duration = "28:10",
            isPremium = false,
            contentPages = listOf(
                NotePage(
                    pageNumber = 1,
                    title = "Page 1: Object-Oriented Programming (OOP)",
                    body = "OOP is a cornerstone of Paper 4. Key properties:\n- Encapsulation: Keeping attributes private, exposing via getter/setter methods.\n- Inheritance: Subclasses inheriting properties from a superclass.\n- Polymorphism: Overriding methods to define custom subclass behavior.",
                    codeSnippet = "# Python OOP template conforming to CIE 9618 syllabus\nclass Student:\n    def __init__(self, name, score):\n        self.__name = name # Private attribute with double underscore\n        self.__score = score\n\n    def get_score(self):\n        return self.__score\n\n    def set_score(self, score):\n        if 0 <= score <= 100:\n            self.__score = score"
                ),
                NotePage(
                    pageNumber = 2,
                    title = "Page 2: Abstract Data Type - Linked List",
                    body = "A Linked List contains nodes. Each node has data and a pointer to the index of the next node. \nWe manage a free pointer list to know where new data can be inserted.",
                    codeSnippet = "# Node and Linked List structure\nclass Node:\n    def __init__(self, data, next_pointer):\n        self.data = data\n        self.next = next_pointer\n\n# 1D array of Nodes represents the linked list structure\nlinked_list = [Node(\"\", -1) for _ in range(10)]\nhead_pointer = 0\nfree_pointer = 0"
                ),
                NotePage(
                    pageNumber = 3,
                    title = "Page 3: Binary Tree Structure & Traversals",
                    body = "A Binary Tree has nodes with up to two child pointers: left and right.\nCommon Traversals:\n- In-order (Left, Root, Right) -> yields sorted order in a BST.\n- Pre-order (Root, Left, Right) -> used for copying trees.\n- Post-order (Left, Right, Root) -> used for deleting trees.",
                    codeSnippet = "# Recursive In-Order traversal\ndef in_order_traverse(node_index, tree_nodes):\n    if node_index != -1:\n        # Traverse Left\n        in_order_traverse(tree_nodes[node_index].left_ptr, tree_nodes)\n        # Process Root\n        print(tree_nodes[node_index].data)\n        # Traverse Right\n        in_order_traverse(tree_nodes[node_index].right_ptr, tree_nodes)"
                )
            )
        ),

        // ==========================================
        // O LEVEL CS 2210 NOTES
        // ==========================================
        PdfNote(
            id = "olevel_p1_systems",
            title = "O-Level P1: Computer Systems & Hardware",
            description = "O-Level Computer Science Theory. Covers Binary, Hex, Logic gates, CPU structure (Von Neumann), RAM, ROM, and secondary storage media.",
            category = "O Level (2210)",
            pages = 3,
            pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            youtubeUrl = "https://www.youtube.com/results?search_query=olevel+computer+science+2210+cpu+von+neumann",
            duration = "14:10",
            isPremium = false,
            contentPages = listOf(
                NotePage(
                    pageNumber = 1,
                    title = "Page 1: Hexadecimal & Binary Conversion",
                    body = "Hexadecimal is a Base-16 system. It uses digits 0-9 and letters A-F to represent values 10-15.\nWhy use Hexadecimal?\n- It is much easier to read and write than long binary strings.\n- It reduces the probability of transcription errors.\n- It makes debugging memory addresses far quicker.",
                    codeSnippet = "# Converting Binary to Hex in Python\nbinary_str = \"11011011\"\ndenary_val = int(binary_str, 2)\nhex_str = hex(denary_val).upper()\nprint(f\"{binary_str} in Hex is {hex_str}\") # Output: 0XDB"
                ),
                NotePage(
                    pageNumber = 2,
                    title = "Page 2: CPU Von Neumann Architecture",
                    body = "The CPU (Central Processing Unit) executes instructions. Key components:\n- ALU (Arithmetic Logic Unit): Performs calculations and logic comparisons.\n- CU (Control Unit): Controls data flow, decodes instructions, sends timing signals.\n- System Clock: Generates pulses to synchronize instructions.\nRegisters:\n- Program Counter (PC): Holds address of next instruction.\n- Memory Address Register (MAR): Holds address of data being read/written.\n- Memory Data Register (MDR): Holds actual data read/written.",
                    codeSnippet = "// The Fetch-Decode-Execute Cycle\n// 1. PC contents copied to MAR. PC incremented.\n// 2. MAR address searched in RAM. Instruction loaded into MDR.\n// 3. MDR contents copied to Instruction Register (CIR).\n// 4. CIR decodes instruction, ALU executes."
                ),
                NotePage(
                    pageNumber = 3,
                    title = "Page 3: Storage Devices",
                    body = "Storage is classified into:\n- Primary Storage: Directly accessible by CPU (RAM: volatile, read-write; ROM: non-volatile, read-only).\n- Secondary Storage: Non-volatile, stores files and OS. (SSD, HDD).\n- Off-line Storage: Removable storage (USB flash drive, external HDD, optical discs).",
                    codeSnippet = null
                )
            )
        ),
        PdfNote(
            id = "olevel_p2_algorithms",
            title = "O-Level P2: Algorithms, Databases & SQL",
            description = "Interactive O-Level Paper 2 guide explaining flowcharts, pseudocode loops, logic gates, database structure, and simple SQL SELECT queries.",
            category = "O Level (2210)",
            pages = 3,
            pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            youtubeUrl = "https://www.youtube.com/results?search_query=olevel+computer+science+2210+databases+sql",
            duration = "16:45",
            isPremium = false,
            contentPages = listOf(
                NotePage(
                    pageNumber = 1,
                    title = "Page 1: Flowchart Symbols & Pseudocode Loops",
                    body = "Flowcharts map algorithms visually:\n- Terminal (Oval): Start/End\n- Input/Output (Parallelogram)\n- Process (Rectangle): Assignment/Math\n- Decision (Diamond): IF-THEN conditions.\n\nLoops in pseudocode:\n1. Count-controlled: FOR ... TO ... NEXT\n2. Pre-condition: WHILE ... DO ... ENDWHILE\n3. Post-condition: REPEAT ... UNTIL",
                    codeSnippet = "// Counting loop in O-Level Pseudocode\nDECLARE Total : INTEGER\nDECLARE Count : INTEGER\nTotal <- 0\nFOR Count <- 1 TO 10\n    Total <- Total + Count\nNEXT Count\nOUTPUT \"Sum of numbers 1-10 is: \", Total"
                ),
                NotePage(
                    pageNumber = 2,
                    title = "Page 2: Logic Gates & Truth Tables",
                    body = "Logic gates are hardware building blocks. Common gates:\n- AND: Outputs 1 only if BOTH inputs are 1.\n- OR: Outputs 1 if ANY input is 1.\n- NOT: Inverts the input.\n- NAND: Opposite of AND.\n- NOR: Opposite of OR.\n- XOR: Outputs 1 if inputs are DIFFERENT.",
                    codeSnippet = "// Truth Table for XOR Gate\n// Input A | Input B | Output Q\n//    0    |    0    |    0\n//    0    |    1    |    1\n//    1    |    0    |    1\n//    1    |    1    |    0"
                ),
                NotePage(
                    pageNumber = 3,
                    title = "Page 3: Databases & SQL Queries",
                    body = "A Database stores structured tables. Each column is a Field; each row is a Record.\nPrimary Key: A field that uniquely identifies each record.\n\nSQL (Structured Query Language) is used to search databases.\nStandard syntax: \nSELECT Field1, Field2 \nFROM TableName \nWHERE Condition ORDER BY Field ASC/DESC",
                    codeSnippet = "-- SQL Query to select passing CS students\nSELECT StudentName, Grade, ExamScore\nFROM StudentTable\nWHERE ExamScore >= 50\nORDER BY ExamScore DESC;"
                )
            )
        )
    )

    fun getNoteById(id: String): PdfNote? {
        return notes.find { it.id == id }
    }

    fun mapBackendNoteToPdfNote(backendNote: BackendNote): PdfNote {
        val title = backendNote.title
        val subject = backendNote.subject

        val template = when {
            title.contains("P3", ignoreCase = true) || title.contains("Paper 3", ignoreCase = true) -> {
                notes.find { it.id == "alevel_p3_networking" }
            }
            title.contains("P4", ignoreCase = true) || title.contains("Paper 4", ignoreCase = true) -> {
                notes.find { it.id == "alevel_p4_adts" }
            }
            title.contains("P2", ignoreCase = true) || title.contains("Paper 2", ignoreCase = true) -> {
                notes.find { it.id == "alevel_p2_programming" }
            }
            title.contains("P1", ignoreCase = true) || title.contains("Paper 1", ignoreCase = true) -> {
                notes.find { it.id == "alevel_p1_data_rep" }
            }
            subject.contains("2210", ignoreCase = true) || subject.contains("O Level", ignoreCase = true) -> {
                if (title.contains("P2", ignoreCase = true)) {
                    notes.find { it.id == "olevel_p2_algorithms" }
                } else {
                    notes.find { it.id == "olevel_p1_systems" }
                }
            }
            else -> null
        } ?: notes.first()

        val mappedCategory = when {
            subject.contains("9618", ignoreCase = true) || subject.contains("A Level", ignoreCase = true) -> "A Level (9618)"
            subject.contains("2210", ignoreCase = true) || subject.contains("O Level", ignoreCase = true) -> "O Level (2210)"
            else -> template.category
        }

        val backendBaseUrl = "https://wbaatz-notes-backend.onrender.com/"
        val fullPdfUrl = if (backendNote.pdfPath.startsWith("http")) {
            backendNote.pdfPath
        } else {
            backendBaseUrl + backendNote.pdfPath
        }

        val fullThumbnailUrl = backendNote.thumbnailPath?.let { path ->
            if (path.startsWith("http")) path else backendBaseUrl + path
        }

        return PdfNote(
            id = backendNote.id,
            title = backendNote.title,
            description = backendNote.description?.takeIf { it.isNotBlank() } ?: template.description,
            category = mappedCategory,
            pages = template.pages,
            pdfUrl = fullPdfUrl,
            thumbnailUrl = fullThumbnailUrl,
            youtubeUrl = template.youtubeUrl,
            duration = template.duration,
            contentPages = template.contentPages,
            isPremium = template.isPremium,
            isLiveApi = true
        )
    }
}
