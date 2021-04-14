export class SearchSettings {
  public searchCriteria?: string[];
  public freshnessEnabled?: boolean;
  public freshness?: number;
  public volume?: number;
  public volumeEnabled?: boolean;
  public relevanceScore?: number;
  public relevanceScoreEnabled?: boolean;
  public freshnessBeginDate?: any;
  public freshnessBeginDateEnabled?: boolean;
  public includedEmailDomains?: [];
  public includedEmailDomainsEnabled?: boolean;
  public excludedEmailDomains?: [];
  public excludedEmailDomainsEnabled?: boolean;
  public useReceivedEmailsContent?: boolean;
  public dataSourceSettings?: {
    dataSourcesPriority: string[];
    isHRDataMandatory: boolean;
  };
  public searchResultsFilters?: {
    dataSource: string;
    filters: [
      {
        filterType: string;
        isActive: boolean;
      }
    ];
  }[];

  constructor(init?: Partial<SearchSettings>) {
    init!.freshnessBeginDate =
      init && init.freshnessBeginDate
        ? new Date(init.freshnessBeginDate * 1000)
        : null;
    Object.assign(this, init);
  }
}
