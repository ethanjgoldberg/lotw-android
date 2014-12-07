package org.anism.lotw;

public interface ActionResolver {
	public boolean getSignedInGPGS();
	public void loginGPGS();
	public void logoutGPGS();
	public void submitScoreGPGS(int score, String board);
	public void unlockAchievementGPGS(String achievementId);
	public void getLeaderboardGPGS(String board);
	public void getAchievementsGPGS();
}
