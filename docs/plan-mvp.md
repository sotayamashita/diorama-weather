# Diorama Weather 実装計画

## 進捗サマリー

| Phase | 状態 | 説明 |
|-------|------|------|
| Phase 1 | ✅ 完了 | ウォーキングスケルトン（固定データでウィジェット表示） |
| Phase 2 | ✅ 完了 | データ層の構築（API、モデル、Repository） |
| Phase 3 | ✅ 完了 | 位置情報サービス（現在地の天気表示） |
| Phase 4 | ✅ 完了 | バックグラウンド更新（WorkManager） |
| Phase 5 | ✅ 完了 | UI の完成（仕様通りのレイアウト、AI生成画像） |
| Phase 6 | ✅ 完了 | アセットと仕上げ（テスト） |

**現在の進捗: 100% 完了**

---

## 決定事項

| 項目 | 決定 |
|------|------|
| バックエンド | MVP では直接 OpenWeatherMap API を呼び出す（Cloudflare Workers は後回し） |
| 画像アセット | MVP では 2-4 パターンで動作確認、40パターンは後から追加 |

---

## Surprises & Discoveries

Document unexpected behaviors, bugs, optimizations, or insights discovered during implementation. Provide concise evidence.

```yaml
- Observation: システムの Java 25 は Kotlin コンパイラが未対応
  Evidence: "IllegalArgumentException: 25.0.1" エラーが発生。Android Studio の JDK 21 を使用することで解決。ビルド時に `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"` を設定する必要あり。

- Observation: Glance の ColorProvider API は Compose の Color クラスを直接受け取る
  Evidence: 当初 `ColorProvider(day=..., night=...)` を使用してエラー。`ColorProvider(Color(...))` で `androidx.compose.ui.graphics.Color` を使用することで解決。

- Observation: エミュレーターの Extended Controls で位置情報を設定しても FusedLocationProviderClient で取得できないことがある
  Evidence: `LocationService: Last known location: null` / `Current location: null` がログに出力。`adb emu geo fix <lon> <lat>` コマンドで直接設定すると確実に反映される。

- Observation: エミュレーターのネットワーク接続が不安定な場合がある
  Evidence: "java.net.UnknownHostException: Unable to resolve host api.openweathermap.org" エラー。Cold Boot で再起動することで解決。

- Observation: Glance の Image composable で PNG と XML drawable の両方がサポートされる
  Evidence: 同名の PNG と XML が存在する場合、PNG が優先される。XML を削除せずに PNG を追加するだけで差し替え可能。

- Observation: WorkManager の PeriodicWorkRequest は最小間隔が 15 分
  Evidence: Android の制限により、15 分未満の間隔は設定できない。30 分間隔で設定し、問題なく動作。
```

---

## Decision Log

Record every decision made while working on the plan in the format:

```yaml
- Decision: MVP ではバックエンド（Cloudflare Workers）を使用せず、直接 OpenWeatherMap API を呼び出す
  Rationale: 開発速度を優先し、最小限の構成で動作確認を行う。API キーは BuildConfig で管理し、セキュリティリスクは許容範囲内。
  Date/Author: 2025-12-04 / Claude

- Decision: 画像アセットは MVP では 2-4 パターンで動作確認、40 パターンは後から追加
  Rationale: 40 パターン全ての生成は時間がかかるため、動作確認を優先。グラデーション XML を fallback として使用。
  Date/Author: 2025-12-04 / Claude

- Decision: LocationService で lastLocation より getCurrentLocation を優先する
  Rationale: エミュレーターでは lastLocation が常に null を返すケースがあった。getCurrentLocation を先に試し、失敗時に lastLocation にフォールバックする方式に変更。
  Date/Author: 2025-12-05 / Claude

- Decision: 背景画像の動的選択は drawable リソース名で行う
  Rationale: `{time_of_day}_{weather}` 形式のファイル名で、Context.getResources().getIdentifier() を使用してリソース ID を取得。存在しない場合は widget_background_default にフォールバック。
  Date/Author: 2025-12-05 / Claude

- Decision: テストは WeatherCodeMapper と TimeOfDay/WeatherType モデルを優先
  Rationale: ビジネスロジックの中核部分をカバー。Repository のテストはモック設定が複雑なため、MVP では省略。
  Date/Author: 2025-12-05 / Claude

- Decision: 設定画面は MVP では省略
  Rationale: MainActivity で位置情報権限のリクエストは実装済み。専用の設定画面は将来の拡張として残す。
  Date/Author: 2025-12-05 / Claude
```

---

## 実装戦略: ウォーキングスケルトン・アプローチ

「動くソフトウェアから始める」戦略を採用。最小限の機能を持つバーティカルスライスを先に完成させ、早期にフィードバックを得られる状態を目指す。

---

## Phase 1: ウォーキングスケルトン（最小動作版） ✅ 完了

**目標**: 固定データでウィジェットが表示される最小限の動作確認

### Slice 1.1: 依存関係とパーミッション設定
- [x] `gradle/libs.versions.toml` に必須ライブラリ追加
- [x] `app/build.gradle.kts` に依存関係追加
- [x] `AndroidManifest.xml` にパーミッション追加

### Slice 1.2: 最小ウィジェット実装（固定データ）
- [x] `res/xml/weather_widget_info.xml` 作成（4x2 サイズ定義）
- [x] `widget/WeatherWidget.kt` 作成
- [x] `widget/WeatherWidgetReceiver.kt` 作成
- [x] `AndroidManifest.xml` にウィジェットレシーバー登録

**完了基準**: ホーム画面に固定テキスト「Tokyo 18° 晴れ」を表示するウィジェットが配置できる ✅

---

## Phase 2: データ層の構築 ✅ 完了

### Slice 2.1: データモデル定義
- [x] `data/model/WeatherResponse.kt` - OpenWeatherMap API レスポンス用
- [x] `data/model/WeatherData.kt` - アプリ内部用（TimeOfDay, WeatherType enum含む）

### Slice 2.2: API 接続
- [x] `data/api/WeatherApi.kt` - Retrofit インターフェース
- [x] `data/api/WeatherApiService.kt` - OkHttp + Retrofit 設定
- [x] `data/api/WeatherCodeMapper.kt` - OpenWeatherMap コードマッピング

### Slice 2.3: Repository パターン
- [x] `data/repository/WeatherRepository.kt`
- [ ] DataStore でキャッシュ（30分有効）← 将来の拡張

---

## Phase 3: 位置情報サービス ✅ 完了

### Slice 3.1: LocationService 実装
- [x] `location/LocationService.kt`
  - FusedLocationProviderClient 使用
  - 最後の既知位置 or 新規リクエスト
- [x] MainActivity で権限リクエスト処理
- [x] `build.gradle.kts` に BuildConfig で API キー設定

### Slice 3.2: ウィジェットとの統合
- [x] ウィジェットから Repository 経由でデータ取得
- [x] 位置情報 → API 呼び出し → UI 更新のフロー完成

**完了基準**: 現在地（または東京）の天気がウィジェットに表示される ✅

---

## Phase 4: バックグラウンド更新 ✅ 完了

### Slice 4.1: WorkManager 統合
- [x] `widget/WeatherWidgetWorker.kt`
  ```kotlin
  class WeatherWidgetWorker(context: Context, params: WorkerParameters)
      : CoroutineWorker(context, params) {
      override suspend fun doWork(): Result {
          // 天気データ取得 & ウィジェット更新
      }
  }
  ```
- [x] 30分間隔の PeriodicWorkRequest 設定
- [x] アプリ起動時・ウィジェット追加時に WorkManager スケジュール
- [x] ウィジェット削除時に WorkManager キャンセル

**完了基準**: 30分ごとに自動更新される ✅

---

## Phase 5: UI の完成 ✅ 完了

### Slice 5.1: ウィジェット UI 完成
- [x] 仕様通りのレイアウト実装（Glance composables）
  - Column + Row で配置
  - 都市名（大）、天気アイコン（絵文字）、日付（小）、気温（中）
- [x] テキストカラー動的切り替え
  - 時間帯・天気に応じて `textColor` / `shadowColor` 適用
- [x] 背景画像表示（Image composable）
- [x] 背景画像の動的選択ロジック（時間帯×天気）

### Slice 5.2: AI生成画像
- [x] Gemini 2.0 Flash で背景画像生成
- [x] `widget_background_default.png` 追加（札幌雪景色ジオラマ）
- [x] グラデーション fallback 画像（8パターン）

### Slice 5.3: 設定画面（MVP では省略）
- [ ] `ui/settings/SettingsScreen.kt`
- [ ] 位置情報権限の確認・リクエスト

**完了基準**: 仕様通りのレイアウトで AI 生成画像が表示される ✅

---

## Phase 6: アセットと仕上げ ✅ 完了

### Slice 6.1: 追加画像アセット（オプション）
- [ ] 時間帯×天気パターンの画像追加（将来の拡張）
  - `morning_sunny.png`
  - `afternoon_sunny.png`
  - `evening_cloudy.png`
  - `night_clear.png`
- [ ] `widget_preview.png` 作成

### Slice 6.2: テスト
- [x] `WeatherCodeMapperTest.kt` - 天気コードマッピングのユニットテスト（15テスト）
- [x] `TimeOfDayTest.kt` - 時間帯判定のユニットテスト（5テスト）
- [x] `WeatherTypeTest.kt` - 天気タイプのユニットテスト（3テスト）

**テスト結果**: 24テスト全て成功 ✅

---

## 実装順序とビルド確認ポイント

各 Phase 完了後にビルドして Emulator で動作確認を行います。

| Phase | 完了後のビルド確認 | 状態 |
|-------|-------------------|------|
| **1** | ウィジェットがホーム画面に追加できる | ✅ |
| **2** | ログに天気データが出力される | ✅ |
| **3** | 現在地の天気がウィジェットに表示される | ✅ |
| **4** | 30分後に自動更新される | ✅ |
| **5** | 仕様通りのレイアウトで表示される | ✅ |
| **6** | テストが通る | ✅ |

**ビルドコマンド**:
```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :app:installDebug
```

**テストコマンド**:
```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :app:testDebugUnitTest
```

---

## 作成済みファイル一覧

### Phase 1 で作成
```
app/src/main/res/xml/weather_widget_info.xml
app/src/main/res/layout/widget_loading.xml
app/src/main/java/com/example/dioramaweather/widget/WeatherWidget.kt
app/src/main/java/com/example/dioramaweather/widget/WeatherWidgetReceiver.kt
```

### Phase 2 で作成
```
app/src/main/java/com/example/dioramaweather/data/model/WeatherResponse.kt
app/src/main/java/com/example/dioramaweather/data/model/WeatherData.kt
app/src/main/java/com/example/dioramaweather/data/api/WeatherApi.kt
app/src/main/java/com/example/dioramaweather/data/api/WeatherApiService.kt
app/src/main/java/com/example/dioramaweather/data/api/WeatherCodeMapper.kt
app/src/main/java/com/example/dioramaweather/data/repository/WeatherRepository.kt
```

### Phase 3 で作成
```
app/src/main/java/com/example/dioramaweather/location/LocationService.kt
```

### Phase 4 で作成
```
app/src/main/java/com/example/dioramaweather/widget/WeatherWidgetWorker.kt
```

### Phase 5 で作成
```
app/src/main/res/drawable/widget_background_default.png (AI生成画像)
app/src/main/res/drawable/morning_sunny.xml
app/src/main/res/drawable/afternoon_sunny.xml
app/src/main/res/drawable/morning_cloudy.xml
app/src/main/res/drawable/afternoon_cloudy.xml
app/src/main/res/drawable/evening_cloudy.xml
app/src/main/res/drawable/evening_rain.xml
app/src/main/res/drawable/night_clear.xml
app/src/main/res/drawable/night_cloudy.xml
```

### Phase 6 で作成
```
app/src/test/java/com/example/dioramaweather/data/api/WeatherCodeMapperTest.kt
app/src/test/java/com/example/dioramaweather/data/model/TimeOfDayTest.kt
app/src/test/java/com/example/dioramaweather/data/model/WeatherTypeTest.kt
```

### 変更済み
- `gradle/libs.versions.toml` - 依存関係バージョン追加
- `app/build.gradle.kts` - 依存関係追加、BuildConfig 設定
- `app/src/main/AndroidManifest.xml` - パーミッション、レシーバー登録
- `app/src/main/res/values/strings.xml` - ウィジェット関連文字列追加
- `app/src/main/java/com/example/dioramaweather/MainActivity.kt` - 権限リクエスト処理、WorkManager スケジュール

---

## 将来の拡張

### 追加画像（オプション）
AI 生成画像を追加する場合は、以下のファイル名で `drawable` フォルダに配置：
- `{time_of_day}_{weather}.png`

### その他
- DataStore によるキャッシュ機能
- 設定画面の実装
- 複数都市対応
- ウィジェットサイズバリエーション (2x2, 4x4)

---

## 注意事項

1. **API キー管理**
   - `local.properties` に `OPENWEATHERMAP_API_KEY=xxx` を追加 ✅
   - `build.gradle.kts` で `buildConfigField` として参照 ✅
   - **絶対にコミットしない**

2. **Java バージョン**
   - ビルド時に `JAVA_HOME` を Android Studio の JDK に設定する必要あり
   - システムの Java 25 は Kotlin コンパイラが未対応

3. **エミュレーター位置情報**
   - Extended Controls の Location で設定しても反映されない場合あり
   - `adb emu geo fix <lon> <lat>` コマンドで設定すると確実

4. **MVP スコープ**
   - バックエンド（Cloudflare Workers）は後回し
   - 画像は 1 パターン + グラデーション fallback で動作確認
   - 設定画面は最小限

---

## 画像生成プロンプト

### 使用サービス
**Google AI Studio** (https://aistudio.google.com/) で Gemini 2.0 Flash を使用

### プロンプトテンプレート（仕様書より）

```
Present a clear, 45° top-down isometric miniature 3D cartoon
scene of a generic modern city, featuring stylized buildings
and urban elements (NO specific landmarks).

Use soft, refined textures with realistic PBR materials and
gentle, lifelike lighting and shadows for [TIME_OF_DAY] time.

Integrate [WEATHER] conditions directly into the city
environment to create an immersive atmospheric mood.

Use a clean, minimalistic composition with a soft,
solid-colored background.

Leave the top-center area clean for text overlay
(city name, weather icon, date, temperature).
```

### MVP 用の 4 パターン

| ファイル名 | TIME_OF_DAY | WEATHER |
|-----------|-------------|---------|
| `afternoon_sunny.png` | afternoon | sunny with partial clouds |
| `night_clear.png` | night | clear starry sky |
| `morning_cloudy.png` | morning | overcast clouds |
| `evening_rain.png` | evening | light rain |

### 画像仕様
- **アスペクト比**: 16:9
- **解像度**: 1920x1080 または 1280x720
- **形式**: PNG
- **保存先**: `app/src/main/res/drawable/`

### 生成手順

1. Google AI Studio にアクセス
2. Gemini 2.0 Flash (Experimental) を選択
3. 上記プロンプトを `[TIME_OF_DAY]` と `[WEATHER]` を置き換えて入力
4. 「Generate」をクリック
5. 生成された画像をダウンロード
6. 必要に応じてリサイズ（16:9、1280x720 推奨）
7. ファイル名を `{time_of_day}_{weather}.png` にリネーム
8. `app/src/main/res/drawable/` にコピー
9. 同名の `.xml` ファイルがあれば削除（PNG が優先される）

### 40パターン全リスト（将来の拡張用）

時間帯（4種）× 天気（10種）= 40パターン

**時間帯**:
- `morning` (6:00-11:59)
- `afternoon` (12:00-16:59)
- `evening` (17:00-19:59)
- `night` (20:00-5:59)

**天気**:
- `clear` (快晴)
- `sunny` (晴れ)
- `cloudy` (曇り)
- `fog` (霧)
- `light_rain` (小雨)
- `rain` (雨)
- `thunderstorm` (雷雨)
- `snow` (雪)
- `blizzard` (吹雪)
- `sleet` (みぞれ)
