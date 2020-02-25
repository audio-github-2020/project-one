package com.pinyougou.user.service;

import com.pinyougou.model.Address;

import java.util.List;

public interface AddressService {

    List<Address> getAddressListByUserId(String userid);
}
