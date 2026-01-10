export interface UserCertificate {
    cn: string,
    email: string,
    city: string,
    ou: string,
    issuer: string,
    serialHex: string,
    validFrom: string,
    validTo: string,
    daysLeft: number
}
