export interface ApprovalChainStep{
    id: number,
    stepNumber: number,
    approverRole: string,
}

export interface ApprovalChain{
    id: number,
    name: string,
    steps: ApprovalChainStep[]
}