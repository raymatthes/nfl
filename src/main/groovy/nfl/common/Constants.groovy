package nfl.common

/**
 * @author Ray Matthes
 */
class Constants {

    static String SURVIVOR_FILE = 'var/survivor.html'
    static String USED_TEAMS_FILE = '/used-teams.yaml'
    static String DOWNLOAD_URL = 'https://www.survivorgrid.com/'

    static enum Name {
        ARI, ATL, BAL, BUF, CAR, CHI, CIN, CLE, DAL, DEN, DET, GB, HOU, IND, JAX, KC,
        LAC, LAR, LV, MIA, MIN, NE, NO, NYG, NYJ, PHI, PIT, SEA, SF, TB, TEN, WAS
    }

    static enum Division {
        AFC_EAST, AFC_NORTH, AFC_SOUTH, AFC_WEST,
        NFC_EAST, NFC_NORTH, NFC_SOUTH, NFC_WEST
    }

    public static Map<Name, Division> Divisions = [
            (Name.BUF): Division.AFC_EAST,
            (Name.MIA): Division.AFC_EAST,
            (Name.NE) : Division.AFC_EAST,
            (Name.NYJ): Division.AFC_EAST,

            (Name.BAL): Division.AFC_NORTH,
            (Name.PIT): Division.AFC_NORTH,
            (Name.CLE): Division.AFC_NORTH,
            (Name.CIN): Division.AFC_NORTH,

            (Name.IND): Division.AFC_SOUTH,
            (Name.JAX): Division.AFC_SOUTH,
            (Name.HOU): Division.AFC_SOUTH,
            (Name.TEN): Division.AFC_SOUTH,

            (Name.KC) : Division.AFC_WEST,
            (Name.LAC): Division.AFC_WEST,
            (Name.LV) : Division.AFC_WEST,
            (Name.DEN): Division.AFC_WEST,

            (Name.PHI): Division.NFC_EAST,
            (Name.DAL): Division.NFC_EAST,
            (Name.WAS): Division.NFC_EAST,
            (Name.NYG): Division.NFC_EAST,

            (Name.DET): Division.NFC_NORTH,
            (Name.GB) : Division.NFC_NORTH,
            (Name.MIN): Division.NFC_NORTH,
            (Name.CHI): Division.NFC_NORTH,

            (Name.TB) : Division.NFC_SOUTH,
            (Name.ATL): Division.NFC_SOUTH,
            (Name.NO) : Division.NFC_SOUTH,
            (Name.CAR): Division.NFC_SOUTH,

            (Name.SF) : Division.NFC_WEST,
            (Name.SEA): Division.NFC_WEST,
            (Name.LAR): Division.NFC_WEST,
            (Name.ARI): Division.NFC_WEST,
    ]

    static enum Name_2019_2020 {
        ARI, ATL, BAL, BUF, CAR, CHI, CIN, CLE, DAL, DEN, DET, GB, HOU, IND, JAX, KC,
        LAC, LAR, MIA, MIN, NE, NO, NYG, NYJ, OAK, PHI, PIT, SEA, SF, TB, TEN, WAS
    }

    static enum Name_2018_2019 {
        ARI, ATL, BAL, BUF, CAR, CHI, CIN, CLE, DAL, DEN, DET, GB, HOU, IND, JAX, KC,
        LAC, LAR, MIA, MIN, NE, NO, NYG, NYJ, OAK, PHI, PIT, SEA, SF, TB, TEN, WAS
    }

    static enum Name_2017_2018 {
        ARI, ATL, BAL, BUF, CAR, CHI, CIN, CLE, DAL, DEN, DET, GB, HOU, IND, JAX, KC,
        LAC, LAR, MIA, MIN, NE, NO, NYG, NYJ, OAK, PHI, PIT, SEA, SF, TB, TEN, WAS
    }

    static enum Name_2016_2017 {
        ARI, ATL, BAL, BUF, CAR, CHI, CIN, CLE, DAL, DEN, DET, GB, HOU, IND, JAX, KC,
        LA, MIA, MIN, NE, NO, NYG, NYJ, OAK, PHI, PIT, SD, SEA, SF, TB, TEN, WAS
    }

    static enum Name_2015_2016 {
        ARI, ATL, BAL, BUF, CAR, CHI, CIN, CLE, DAL, DEN, DET, GB, HOU, IND, JAX, KC,
        MIA, MIN, NE, NO, NYG, NYJ, OAK, PHI, PIT, SD, SEA, SF, STL, TB, TEN, WAS
    }

    static enum Name_2014_2015 {
        ARI, ATL, BAL, BUF, CAR, CHI, CIN, CLE, DAL, DEN, DET, GB, HOU, IND, JAX, KC,
        MIA, MIN, NE, NO, NYG, NYJ, OAK, PHI, PIT, SD, SEA, SF, STL, TB, TEN, WAS
    }

    static final int FINAL_WEEK = 18

    static final BigDecimal SPIKE = BigDecimal.valueOf(1000L)

    static final int DOUBLE_PICKS_START_WEEK = 5

}
