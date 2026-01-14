# Panduan Publikasi Library ke JitPack

Dokumen ini menjelaskan langkah-langkah dan persyaratan untuk merubah SDK lokal ini menjadi library standar yang bisa di-install melalui Maven/Gradle menggunakan JitPack.

## 1. Persyaratan Utama (Requirements)
Agar project ini bisa dibaca oleh JitPack sebagai library, ada beberapa hal yang wajib dipenuhi:

- **Android Library Plugin:** Project harus menggunakan `id 'com.android.library'` (sudah dikonfigurasi).
- **Maven Publish Plugin:** Digunakan untuk membuat "resep" (metadata) library agar dikenal oleh sistem Maven.
- **Daftar Dependensi yang Jelas:** Menghindari penggunaan `libs.xxx` (Version Catalog) di dalam library jika ingin distribusi yang lebih portable, atau pastikan file catalog ikut terunggah (lebih aman pakai hardcoded version untuk library publik sederhana).
- **Public Repository:** Library harus di-push ke GitHub (publik) agar bisa diakses oleh JitPack secara gratis.
- **Gradle Wrapper:** Project wajib memiliki file `gradlew` dan folder `gradle/wrapper` di root agar JitPack bisa menjalankan build otomatis.

### 1.5 Generate Gradle Wrapper (Penting!)
Jika kamu tidak melihat file `gradlew` di root project-mu, kamu harus membuatnya terlebih dahulu melalui Terminal di Android Studio:
```bash
gradle wrapper --gradle-version 8.9
```
*Pastikan file `gradlew`, `gradlew.bat`, dan folder `gradle/` ikut di-commit dan di-push ke GitHub.*

## 2. Langkah-Langkah Teknis

### A. Konfigurasi `build.gradle` (Sudah Dilakukan)
Pastikan block ini ada di `build.gradle` module library-mu:
```groovy
plugins {
    id 'com.android.library'
    id 'maven-publish'
}

afterEvaluate {
    publishing {
        publications {
            release(MavenPublication) {
                from components.release // Mengambil output build release (.aar)
                groupId = 'com.github.nunutech40'
                artifactId = 'liveness-sdk'
                version = '1.0.0'
            }
        }
    }
}
```

### B. Membuat GitHub Release
1. Push semua code ke branch utama (`main` atau `master`).
2. Buka repo-mu di GitHub.
3. Klik **"Create a new release"** di bar sebelah kanan.
4. Masukkan tag (misal: `v1.0.0`).
5. Klik **Publish release**.

### C. Mendaftarkan di JitPack
1. Buka [jitpack.io](https://jitpack.io).
2. Masukkan URL repository GitHub-mu (misal: `github.com/nunutech40/android-liveness-sdk`).
3. Klik **Get it**.
4. JitPack akan mencoba men-build project-mu. Jika log berwarna hijau (Log Icon), artinya berhasil!

## 3. Cara Penggunaan di Project Lain
Setelah terdaftar, orang lain cukup menambahkan ini di `build.gradle` mereka:
```kotlin
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.nunutech40:android-liveness-sdk:v1.0.0'
}
```

---

## 🧐 Penjelasan Folder `.gradle` (Versi 8.9 dan 9.2.0)

Kamu mungkin melihat folder `8.9` dan `9.2.0` di dalam folder hiddent `.gradle`. Berikut penjelasannya:

1.  **Apa itu folder tersebut?**  
    Folder di dalam `.gradle/` adalah **Build Cache** dan **Metadata** yang dibuat oleh Gradle saat kamu menjalankan perintah build. Gradle memisahkan cache ini berdasarkan versinya agar tidak bentrok.

2.  **Kenapa ada dua versi?**
    -   **8.9**: Ini adalah versi Gradle stabil yang kemungkinan besar kamu gunakan saat ini untuk build project.
    -   **9.2.0**: Folder ini biasanya bukan merujuk pada versi Gradle (karena Gradle 9 belum umum/stabil), melainkan **versi Plugin atau Tooling** tertentu yang berjalan di atas Gradle. 
    -   Misalnya: Jika kamu menggunakan **Kotlin 1.9.20** atau plugin tertentu yang memiliki metadata khusus, terkadang folder dengan penomoran ini muncul. Namun, jika itu adalah folder di root `.gradle/`, kemungkinan besar ada tugas (task) yang pernah dijalankan menggunakan instance Gradle yang berbeda (mungkin dari IDE yang ter-update atau script manual).

3.  **Apakah aman dihapus?**  
    Sangat aman. Folder `.gradle` hanyalah cache. Jika kamu hapus, build berikutnya mungkin terasa sedikit lebih lama karena Gradle harus men-download ulang dependencies dan melakukan indexing ulang, tapi tidak akan merusak kodinganmu.
