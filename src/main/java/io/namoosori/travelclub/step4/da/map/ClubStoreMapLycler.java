package io.namoosori.travelclub.step4.da.map;

import io.namoosori.travelclub.step4.da.maria.mybatis.MembershipMariaStore;
import io.namoosori.travelclub.step4.store.*;

public class ClubStoreMapLycler implements ClubStoreLycler {

    private static ClubStoreLycler lycler;

    private ClubStoreMapLycler(){}

    public static ClubStoreLycler getInstance(){
        if(lycler == null)
            lycler = new ClubStoreMapLycler();

        return lycler;
    }

    /*public static ClubStoremariaLycler getInstance(){
        if(lycler == null)
            lycler = new ClubStoremariaLycler();

        return lycler;
    }*/

    @Override
    public MemberStore requestMemberStore() {
        return new MemberMapStore();
    }

    @Override
    public ClubStore requestClubStore() {
        return new ClubMapStore();
    }

    @Override
    public BoardStore requestBoardStore() {
        return new BoardMapStore();
    }

    @Override
    public PostingStore requestPostingStore() {
        return new PostingMapStroe();
    }

    @Override
    public MembershipStore requsetMembershipStore() {
        return new MembershipMariaStore();
    }
}
