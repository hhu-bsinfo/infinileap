#ifndef RDMA_REFLECTIONUTILITY_HPP
#define RDMA_REFLECTIONUTILITY_HPP

#include <unordered_map>

class ReflectionUtility {

public:

    struct MemberInfo {
        char name[32];
        int offset;
    } __attribute__ ((packed));

    struct StructInfo {
        int structSize;
        int memberCount;
        MemberInfo *memberInfos;
    } __attribute__ ((packed));

    ReflectionUtility() = delete;

    static StructInfo* getStructInfo(std::string identifier);

private:

    static std::unordered_map<std::string, ReflectionUtility::StructInfo*> structInfos;

};

#endif //RDMA_REFLECTIONUTILITY_HPP
