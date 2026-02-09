export interface ApprovalChainStep{
    id: number,
    order: number,
    role: string,
    reference: number
}

export interface ApprovalChain{
    id: number,
    name: string,
    steps: ApprovalChainStep[]
}