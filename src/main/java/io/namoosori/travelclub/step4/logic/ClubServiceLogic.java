package io.namoosori.travelclub.step4.logic;

import io.namoosori.travelclub.step1.entity.club.ClubMembership;
import io.namoosori.travelclub.step1.entity.club.CommunityMember;
import io.namoosori.travelclub.step1.entity.club.RoleInClub;
import io.namoosori.travelclub.step1.entity.club.TravelClub;
import io.namoosori.travelclub.step4.da.ClubStoreMasterLycler;
import io.namoosori.travelclub.step4.da.map.ClubStoreMapLycler;
import io.namoosori.travelclub.step4.service.ClubService;
import io.namoosori.travelclub.step4.service.dto.ClubMembershipDto;
import io.namoosori.travelclub.step4.service.dto.TravelClubDto;
import io.namoosori.travelclub.step4.store.ClubStore;
import io.namoosori.travelclub.step4.store.MemberStore;
import io.namoosori.travelclub.step4.util.ClubDuplicationException;
import io.namoosori.travelclub.step4.util.MemberDuplicationException;
import io.namoosori.travelclub.step4.util.NoSuchClubException;
import io.namoosori.travelclub.step4.util.NoSuchMemberException;
import io.namoosori.travelclub.util.StringUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClubServiceLogic implements ClubService {
    private ClubStore clubStore;
    private MemberStore memberStore;

    public ClubServiceLogic() {

//        clubStore = ClubStoreMapLycler.getInstance().requestClubStore();
//        memberStore = ClubStoreMapLycler.getInstance().requestMemberStore();
        clubStore = ClubStoreMasterLycler.getInstance().requestClubStore();
        memberStore = ClubStoreMasterLycler.getInstance().requestMemberStore();
    }

    @Override
    public void registerClub(TravelClubDto clubDto) {
        Optional.ofNullable(clubStore.retrieveByName(clubDto.getName()))
                .ifPresent(dto -> {
                    throw new ClubDuplicationException("Club already exists with name:" + clubDto.getName());
                });

        TravelClub club = clubDto.toTravelClub();
        String clubId = clubStore.create(club);

        clubDto.setUsid(clubId);
    }

    @Override
    public TravelClubDto findClub(String clubId) {

        return Optional.ofNullable(clubStore.retrieve(clubId))
                .map(club -> new TravelClubDto(club))
                .orElseThrow(() -> new NoSuchClubException("No such club with id: " + clubId));
    }

    @Override
    public TravelClubDto findClubByName(String name) {
        return Optional.ofNullable(clubStore.retrieveByName(name))
                .map(club -> new TravelClubDto(club))
                .orElseThrow(() -> new NoSuchClubException("No such club with name: " + name));
    }

    @Override
    public void modify(TravelClubDto clubDto) {
        Optional.ofNullable(clubStore.retrieveByName(clubDto.getName()))
                .ifPresent(club -> {
                    throw new ClubDuplicationException("Club already exists with name:" + clubDto.getName());
                });

        TravelClub targetClub = Optional.ofNullable(clubStore.retrieve(clubDto.getUsid()))
                .orElseThrow(() -> new NoSuchClubException("No such club with id: " + clubDto.getUsid()));
        if (StringUtil.isEmpty(clubDto.getName())) {
            clubDto.setName(targetClub.getName());
        }
        if (StringUtil.isEmpty(clubDto.getIntro())) {
            clubDto.setIntro(targetClub.getIntro());
        }

        clubStore.update(clubDto.toTravelClub());
    }

    @Override
    public void remove(String clubId) {
        if (!clubStore.exists(clubId)) {
            throw new NoSuchClubException("No such club with id: " + clubId);
        }

        clubStore.delete(clubId);
    }

    //@Override
    public void addMembership(ClubMembershipDto membershipDto) {
        String memberId = membershipDto.getMemberEmail();

        CommunityMember member = Optional.ofNullable(memberStore.retrieve(memberId))
                .orElseThrow(() -> new NoSuchClubException("No such member with email: " + memberId));

        TravelClub club = clubStore.retrieve(membershipDto.getClubId());
        for (ClubMembership membership : club.getMembershipList()) {
            if (memberId.equals(membership.getMemberEmail())) {
                throw new MemberDuplicationException("Member already exists in the club -->" + memberId);
            }
        }

        ClubMembership clubMembership = membershipDto.toMembership();
        club.getMembershipList().add(clubMembership);
        clubStore.update(club);
        member.getMembershipList().add(clubMembership);
        memberStore.update(member);
    }

    //@Override
    public ClubMembershipDto findMembershipIn(String clubId, String memberId) {
        TravelClub club = clubStore.retrieve(clubId);
        ClubMembership membership = getMembershipIn(club, memberId);

        return new ClubMembershipDto(membership);

    }

    //@Override
    public List<ClubMembershipDto> findAllMembershipsIn(String clubId) {

        TravelClub club = clubStore.retrieve(clubId);

        return club.getMembershipList().stream()
                .map(membership -> new ClubMembershipDto(membership))
                .collect(Collectors.toList());
    }

    //@Override
    public void modifyMembership(String clubId, ClubMembershipDto newMembership) {
        String targetEmail = newMembership.getMemberEmail();
        RoleInClub newRole = newMembership.getRole();

        TravelClub targetClub = clubStore.retrieve(clubId);
        ClubMembership membershipOfClub = getMembershipIn(targetClub, targetEmail);
        membershipOfClub.setRole(newRole);
        clubStore.update(targetClub);

        CommunityMember targetMember = memberStore.retrieve(targetEmail);
        targetMember.getMembershipList().stream().forEach(membershipOfMember -> {
            if(membershipOfClub.getClubId().equals(clubId)){
                membershipOfClub.setRole(newRole);
            }
        });

        memberStore.update(targetMember);
    }

    //@Override
    public void removeMembership(String clubId, String memberId) {
        TravelClub foundClub = clubStore.retrieve(clubId);
        CommunityMember foundMember = memberStore.retrieve(memberId);
        ClubMembership clubMembership = getMembershipIn(foundClub, memberId);

        foundClub.getMembershipList().remove(clubMembership);
        clubStore.update(foundClub);
        foundMember.getMembershipList().remove(clubMembership);
        memberStore.update(foundMember);
    }

    private ClubMembership getMembershipIn(TravelClub club, String memberEmail) {
        for( ClubMembership membership : club.getMembershipList()){
            if(memberEmail.equals(membership.getMemberEmail()))
                return membership;
        }

        String message = String.format("No such member[email:%s] in club[name:%s]", memberEmail, club.getName());
        throw new NoSuchMemberException(message);
    }
}
