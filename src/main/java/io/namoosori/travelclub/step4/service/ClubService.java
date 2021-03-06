package io.namoosori.travelclub.step4.service;

import io.namoosori.travelclub.step4.service.dto.ClubMembershipDto;
import io.namoosori.travelclub.step4.service.dto.TravelClubDto;

import java.util.List;

public interface ClubService {
    public void registerClub(TravelClubDto clubDto);
    public TravelClubDto findClub(String clubId);
    public TravelClubDto findClubByName(String name);
    public void modify(TravelClubDto clubDto);
    public void remove(String clubId);
}
