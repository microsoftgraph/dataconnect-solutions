/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

import store from '@/store';

export function isSkillSearched(terms: string[], skill: string) {
  return (
    terms.find((sk: string) => {
      return skill.toLowerCase() === sk.toLowerCase();
    }) !== undefined
  );
}

export class Employee {
  public id?: string;
  public name?: string = '';
  public department?: string;
  public mail?: string;
  public role?: string;
  public location?: string;
  public timeZone?: string;
  public timeNow?: string;
  public about?: string;
  public topics?: string[] = [];
  public inferredRoles?: string[] = [];
  public highlightedTerms?: string[] = [];
  public inferredSkills?: {
    highlighted: boolean;
    value: string;
  }[];
  public relevantSkills?: {
    highlighted: boolean;
    value: string;
  }[];
  public relevantSkillsHTML: any;
  public declaredSkills?: {
    highlighted: boolean;
    value: string;
  }[];
  public domainToSkillMap: any;
  public domainToSkillMapHTML: any;
  public currentEngagement?: string;
  public reportsTo?: string;
  public availableSince?: string;
  public profilePicture?: string;
  public showMore?: boolean = true;
  public isSkillEllipsis?: boolean = false;
  public includedInCurrentTeam?: boolean = false;
  public managerEmail?: string = '';
  public linkedInProfile?: string = '';

  constructor(init?: Partial<Employee>) {
    Object.assign(this, init);
    if (init?.relevantSkills) {
      this.relevantSkills = [...new Set(init.relevantSkills)].map(
        (skill: any) => {
          return {
            highlighted: isSkillSearched(this.highlightedTerms!, skill),
            value: skill
          };
        }
      );
      this.relevantSkillsHTML =
        `<span class="mr-1 text-black text-r"
      >Profile Skills:</span
    >` +
        [...new Set(init.relevantSkills)]
          .map((skill: any, index: number) => {
            const isSkillHighlighted = isSkillSearched(
              this.highlightedTerms!,
              skill
            );
            if (isSkillHighlighted) {
              return ` <span
      class="skill highlighted-skill"
      >${skill}${
                index + 1 !== init.relevantSkills!.length
                  ? '<span class="text-secondary custom-comma">,</span>'
                  : ''
              }</span
    >`;
            } else {
              return `${skill}${
                index + 1 !== init.relevantSkills!.length ? ',' : ''
              } `;
            }
          })
          .join('');
    }
    if (init?.declaredSkills)
      this.declaredSkills = [...new Set(init.declaredSkills)].map(
        (skill: any) => {
          return {
            highlighted: isSkillSearched(this.highlightedTerms!, skill),
            value: skill
          };
        }
      );
    if (init?.inferredSkills)
      this.inferredSkills = [...new Set(init.inferredSkills)].map(
        (skill: any) => {
          return {
            highlighted: isSkillSearched(this.highlightedTerms!, skill),
            value: skill
          };
        }
      );
    if (init?.domainToSkillMap) {
      let empDomainToSkillMapHTML: any = {};
      Object.keys(init.domainToSkillMap).map(domain => {
        if (init.domainToSkillMap[domain].length !== 0) {
          const domainArr = [...new Set(init.domainToSkillMap[domain])].filter(
            x => x
          );
          if (domainArr.length !== 0)
            empDomainToSkillMapHTML[domain] =
              `  <span class="mr-1 text-black text-r"
            >${(store.state as any).team.taxonomies[domain]} Skills:</span
          >` +
              domainArr
                .map((skill: any, index: number) => {
                  const isSkillHighlighted = isSkillSearched(
                    this.highlightedTerms!,
                    skill
                  );
                  if (isSkillHighlighted) {
                    return ` <span
              class="skill highlighted-skill"
              >${skill}${
                      index + 1 !== domainArr.length
                        ? '<span class="text-secondary custom-comma">,</span>'
                        : ''
                    }</span
            >`;
                  } else {
                    return `${skill}${
                      index + 1 !== domainArr.length ? ',' : ''
                    } `;
                  }
                })
                .join('');
        }
      });
      this.domainToSkillMapHTML = empDomainToSkillMapHTML;
    }
  }
}

export class TeamMember {
  public userId?: string;
  public employeeId?: string;
  public teamMemberId?: string;
  public name?: string;
  public email?: string;
  public skills?: string[];
  public availableSince?: string;
  public employeeRole?: string;
  public employeeProfilePicture?: string;

  constructor(init?: Partial<TeamMember>) {
    Object.assign(this, init);
  }
}
