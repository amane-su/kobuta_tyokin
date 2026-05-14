# こぶた貯金の使い方


## タイトル画面

- アプリを最初に開いた際にタイトル画面が表示されます。
- 画面内をタップすることで次の画面に遷移することができます。

![title](https://github.com/user-attachments/assets/55d20296-7d87-4f8c-88d6-0e9f5b67d0e9)


## 目標貯金額設定画面

- アプリの初回起動時にタイトル画面の次に表示される画面です。
- 目標となる貯金額を入力し設定することで、アプリ内ではその目標額に向けて貯金を進めていくことになります。
- 決定ボタンをタップすることで次の画面に遷移することができます。

![スクリーンショット 2025-01-24 161918](https://github.com/user-attachments/assets/9ac20e7b-cd85-4d0a-b30f-0d03148a8f8d)

## 名前設定画面

- 目標貯金額設定画面と同様にアプリの初回起動時に表示される画面です。
- 自分が育てるこぶたの名前を自由に設定することができます。
- 決定ボタンをタップすることで次の画面に遷移することができます。

![スクリーンショット 2025-01-24 161944](https://github.com/user-attachments/assets/b5213f60-89b7-487d-b081-29ca0f06fdb1)

## メイン画面

- 貯金状況を一目で確認できます。
- 画面下部にあるお金ボタンを押すことで貯金することができる。

![スクリーンショット 2025-01-24 162030](https://github.com/user-attachments/assets/0973c0d0-c430-4993-a1f4-60b469038da9)

### アプリ内での貯金の定義

アプリ内で貯金する額を自己申告

貯金したいときに貯金したい額を貯金

### アプリ内での貯金とは？

銀行口座の中で“使わない”と決めた額を貯金した額として取り扱います

## グラフ画面

- 貯金の進捗状況をグラフ化表示することで、自分の貯金状況を見返せるようになっています。
- 標準では直近一週間のデータを表示していますが、期間を選択することで自分の見たい期間のグラフを表示することができ、過去の貯金の進捗状況も確認することができるようになっています。


![スクリーンショット 2024-12-20 112307](https://github.com/user-attachments/assets/1366f35d-7ea1-4ec2-b845-44563aef3c22) ![スクリーンショット 2024-12-20 112347](https://github.com/user-attachments/assets/2da16ead-0ad5-43db-9e77-04c116b5cabd)



## ミッション画面

- 日々の貯金目標やタスクが表示されており、一つのモチベーション要素となっています。
- ミッションを達成することでゲーム内コインを獲得することができ、アイテムを購入する際に使用することができます。

 ![スクリーンショット 2025-01-31 010808](https://github.com/user-attachments/assets/35719bbe-61c0-47cc-a005-4c336dfbeb15)


## 設定画面

- 様々な設定を行うことができる画面です。
- 内容としては、通知のオンオフ設定、目標貯金額の再設定、こぶたの名前変更です。

 ![スクリーンショット 2025-01-31 010849](https://github.com/user-attachments/assets/8919d351-ff49-422c-b6ca-019035357d24)


## アイテム画面

- 貯金の達成度、ゲーム内コインに応じて取得できるアイテムを確認できます。
- アイテムを獲得し、育てているこぶたを着せ替えることが可能です。

![image](https://github.com/user-attachments/assets/18a1dcde-7112-45dd-a133-489497bcc09f)

## 庭画面

- 以前達成した貯金のこぶたが庭で暮らす様子を表示します。
- この画面を見ることで、今までどのくらい貯金を達成できたか、視覚的に確認できます。

![image](https://github.com/user-attachments/assets/d858f183-59ab-477d-b40a-5a5a19467846)

--------
## 外部サービス、オープンソースソフトウェアの紹介

### MPAndroidChart

- Android 向けのオープンソースのグラフ描画ライブラリです。
- 折れ線グラフ、棒グラフ、円グラフなどさまざまなチャートを簡単に描画できます。

- インストール方法は以下のようになる。

build.gradle.kts（Module: app）に以下を追加し、同期(Sync)します。

```bash
dependencies {
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
```

詳細な使用方法については、[公式ドキュメント](https://github.com/PhilJay/MPAndroidChart/wiki)を参照してください。



### MoneytreeのLINK API

- 銀行の口座残高や取引履歴・クレジットカード情報を取得可能で、最新の金融データがリアルタイムで利用できます。
- 今回のアプリではサンプルデータを使用することができ、そのサンプルデータで仮の情報を設定することで、残高情報を取得しています。

- サンプルデータは以下のように設定されている。

assetsフォルダにbank.jsonというファイルを作り、以下を記述する。

 ```bash
{
  "account_balances": [
    {
      "id": 1234567891,
      "account_id": 12345678,
      "date": "2023-02-16",
      "balance": 100000.0,
      "balance_in_base": 100000.0,
      "data_source": "institution",
      "balance_type": 0
    },
  ]
}
```

詳細な情報については、[Moneytree LINK製品・技術概要](https://docs.link.getmoneytree.com/docs/product-and-tech-overview?utm_campaign=Sample+Data+Files&utm_source=email&utm_content=EMAIL+0+-+Sample+data+-+Send+sample+data+file+-+points)を参照してください。
