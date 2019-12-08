# PasswordList
オンライン上にパスワードを保存し，デバイス間で共有できます<br>
Android版は本体にもパスワードを保存できます．
### 対応OS
- Android
- Windows

## PasswordListAndroid
Android版クライアント用プロジェクトディレクトリ<br>
`AndroidStudio`で開くことが出来ます

## PasswordListWin
Windows版クライアント用プロジェクトディレクトリ<br>
`Visual Studio`で開くことが出来ます

## PasswordList
サーバーアプリケーション<br>
`Tomcat`用プロジェクトになっています
___

# 利用方法
## サーバー
1. サーバーにするPCにTomcatをインストール(Raspberry Pi等)
1. eclipse等でwarファイルにエクスポートする
1. Tomcatサーバーにデプロイ(`./tomcat/webapps`辺りにコピー)
## Android
1. `再取得`ボタンを長押し
1. 接続先編集を選択し，接続先を登録する
- ローカルのみで使用する場合はそのまま使用できます
## Windows
1. 一度起動すると実行ファイルと同じディレクトリに設定ファイルが出現します(`ConnectionData.json`)
1. 一旦アプリを終了
1. 設定ファイルを書き換え再度起動すると使用できます

設定項目

|Name|Address|PortNo|Key64|
|:--:|:--:|:--:|:--:|
|接続名称|接続先アドレス|ポート番号|暗号キー|


# ライセンス
このソフトウェアは、MITライセンスのもとで公開されています