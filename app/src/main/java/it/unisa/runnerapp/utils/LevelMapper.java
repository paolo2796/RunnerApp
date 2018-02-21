package it.unisa.runnerapp.utils;

public class LevelMapper
{

    private static final String BEGINNER_LEVEL="Amatore";
    private static final String PASSIONATE_LEVEL="Appassionato";
    private static final String PRO_LEVEL="Professionista";
    private static final String ERROR_LEVEL="Livello non corretto";

    public static String getLevelName(int level)
    {
        switch (level)
        {
            case 1:
                return BEGINNER_LEVEL;
            case 2:
                return PASSIONATE_LEVEL;
            case 3:
                return PRO_LEVEL;
            default:
                return ERROR_LEVEL;
        }
    }
}
