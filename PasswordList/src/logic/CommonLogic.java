package logic;

import entity.PasswordItem;

/**
 * ロジッ共通
 */
class CommonLogic {
	/**
	 * 配列を生成する（引数がnullの場合は長さ0の配列を生成）
	 *
	 * @param passwordItem 配列に格納するアイテム
	 * @return 配列
	 */
	static PasswordItem[] CreateList(PasswordItem passwordItem) {
		if (passwordItem == null) return new PasswordItem[0];
		return new PasswordItem[] { passwordItem };
	}
}
