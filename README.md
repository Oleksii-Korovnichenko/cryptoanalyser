# 🔐 CryptoAnalyser
Java-додаток для шифрування, дешифрування та криптоаналізу текстових файлів 
(шифр Цезаря + інші варіації). Підтримується **консольний режим** і 
**графічний інтерфейс (GUI)** на основі JavaFX.

## 🚀 Як розпочати роботу

### 📋 Попередні вимоги
- Java 17 або новіша
- Maven 3.8+
- JavaFX SDK (для запуску GUI)
---
## 📦 Встановлення та запуск

### Через Maven
bash
mvn clean package
Файл cryptoanalyser.jar буде створено в папці target/.

⚙️ Формат команд (консольний режим)
[команда] шлях_до_файлу ключ [шифр] [алфавіт]

✅ Доступні команди:
- encrypt — зашифрувати текстовий файл
- decrypt — розшифрувати файл
- brute_force — зламати шифр Цезаря методом частотного аналізу

🔐 Приклади використання
✅ Caesar (за замовчуванням):
encrypt input.txt 3
decrypt input.txt 3

🔡 Вказання шифру та алфавіту:
encrypt input.txt 3 caesar UKR_WITH_NUMBERS_PUNCT

🧠 Brute Force з аналізом:
brute_force input.txt fileForStaticAnalysis.txt
за замовченням файл для аналізу повинен знаходитися в той же директорії де .jar
і обов'язково з іменем static-analysis.txt

🌐 Доступні алфавіти:
- ENG
- UKR
- ENG_WITH_NUMBERS
- UKR_WITH_NUMBERS
- ENG_WITH_NUMBERS_PUNCT
- UKR_WITH_NUMBERS_PUNCT
- MIXED

🖥 Графічний інтерфейс (GUI)\
Програма підтримує графічний режим роботи через JavaFX.\
▶️ Запуск GUI через Maven\
mvn javafx:run -Dexec.mainClass="com.javarush.cryptoanalyzer.korovnichenko.EntryPoint"

▶️ Запуск за допомогою JAR\
java --module-path /шлях/до/javafx-sdk-17/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar cryptoanalyser.jar
і потім в консолі набрати gui

🔸 gui вказує програмі запускати графічний режим,
за замовченням консольний режим.

🧩 Можливості GUI
- Вибір операції: encrypt, decrypt, brute_force
- Введення шляху до файлу
- Введення ключа
- Вибір типу шифру
- Вибір типу алфавіту
- Відображення результату
- Збереження результату в окремий файл
  ![image](https://github.com/user-attachments/assets/ae17bd88-7cd4-4d9d-b1b1-7f2b15315a72)

🧪 Приклад запуску з JavaFX вручну
java --module-path /шлях/до/javafx-sdk-17/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar cryptoanalyser.jar gui

🤝 Автор
Oleksii Korovnichenko
2025 © Усі права захищені
