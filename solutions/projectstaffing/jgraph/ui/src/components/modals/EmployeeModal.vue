<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <b-modal
    id="user-profile-modal"
    centered
    dialog-class="default-modal"
    headerClass="border-bottom-0"
    scrollable
    @change="init"
    @hide="closed"
  >
    <template v-slot:modal-header="{ close }">
      <b-icon-x @click="close"> </b-icon-x>
    </template>
    <div v-if="loaded">
      <b-row no-gutters>
        <b-avatar
          :src="'data:image/jpeg;base64,' + employee.profilePicture"
          :style="{
            backgroundColor: getRandomColor(
              employee.name
                ? employee.name.charCodeAt(employee.name.length - 1)
                : 0
            )
          }"
          class="mr-3"
          :text="initials"
        ></b-avatar>
        <b-col align-self="center" no-gutters>
          <h5 class="text-sb">{{ employee.name }}</h5>
          <span>{{ employee.role }}</span>
          <div class="text-secondary mt-2">
            <b-icon-geo-alt class="mr-1"></b-icon-geo-alt>
            <span>
              {{ employee.location }}
            </span>
          </div>
          <div class="text-secondary">
            <a :href="`mailto:${employee.mail}`">
              <b-icon-envelope class="mr-1"></b-icon-envelope>
              <span>
                {{ employee.mail }}
              </span>
            </a>
          </div>
          <div class="linkedin mt-1" v-if="employee.linkedInProfile">
            <a :href="employee.linkedInProfile" target="_blank">
              <svg
                width="16px"
                height="16px"
                viewBox="0 0 16 16"
                version="1.1"
                xmlns="http://www.w3.org/2000/svg"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xml:space="preserve"
                xmlns:serif="http://www.serif.com/"
              >
                <rect
                  id="ico-16-linkedin-selected"
                  x="0"
                  y="0"
                  width="16"
                  height="16"
                  style="fill:none;"
                />
                <path
                  d="M14,3.487c0,-0.821 -0.666,-1.487 -1.487,-1.487l-9.026,0c-0.821,0 -1.487,0.666 -1.487,1.487l0,9.026c0,0.821 0.666,1.487 1.487,1.487l9.026,0c0.821,0 1.487,-0.666 1.487,-1.487l0,-9.026Zm-0.7,0l0,9.026c0,0.434 -0.353,0.787 -0.787,0.787l-9.026,0c-0.434,0 -0.787,-0.353 -0.787,-0.787l-0,-9.026c-0,-0.434 0.353,-0.787 0.787,-0.787c0,-0 9.026,-0 9.026,-0c0.434,-0 0.787,0.353 0.787,0.787Zm-7.215,7.879l-0,-4.542l-1.51,-0l0,4.542l1.51,-0Zm2.331,-0l-0,-2.537c-0,-0.136 0.01,-0.271 0.05,-0.368c0.109,-0.271 0.357,-0.552 0.774,-0.552c0.547,-0 0.765,0.416 0.765,1.027l-0,2.43l1.509,-0l-0,-2.605c-0,-1.396 -0.744,-2.045 -1.738,-2.045c-0.8,0 -1.159,0.44 -1.36,0.75l-0,0.016l-0.011,0c0.004,-0.005 0.007,-0.01 0.011,-0.016l-0,-0.642l-1.509,-0c0.019,0.426 -0,4.542 -0,4.542l1.509,-0Zm-3.086,-5.163c0.526,0 0.854,-0.348 0.854,-0.784c-0.01,-0.446 -0.327,-0.785 -0.844,-0.785c-0.516,0 -0.854,0.34 -0.854,0.785c0,0.436 0.328,0.784 0.834,0.784l0.01,0Z"
                />
              </svg>
              LinkedIn Profile
            </a>
          </div>
        </b-col>
      </b-row>
      <b-col class="p-0 mt-4" v-if="employee.about">
        <h6 class="mb-2 text-sb">About</h6>
        <div v-html="employee.about"></div>
      </b-col>
      <b-col
        no-gutters
        class="p-0 mt-4"
        v-if="employee.declaredSkills && employee.declaredSkills.length"
      >
        <h6 class="mb-2 text-sb">Skills</h6>
        <div>
          <span
            v-for="(skill, index) in employee.declaredSkills"
            :key="`${employee.name}_profille_skill_${skill.value}_${index}`"
            >{{ skill.value.trim()
            }}{{ index + 1 === employee.declaredSkills.length ? '' : ',' }}
          </span>
        </div>
      </b-col>
      <b-col
        no-gutters
        class="p-0 mt-4"
        v-if="employee.topics && employee.topics.length"
      >
        <h6 class="mb-2 text-sb">Topics of Interest</h6>
        <div>
          <span
            v-for="(topic, index) in employee.topics"
            :key="`${employee.name}_topic_${topic}_${index}`"
            >{{ topic.trim()
            }}{{ index + 1 === employee.topics.length ? '' : ',' }}
          </span>
        </div>
      </b-col>

      <b-col no-gutters class="p-0 mt-4 mb-4 text-secondary">
        <div v-if="employee.currentEngagement">
          Currently part of the
          <span class="text-sb">{{ employee.currentEngagement }}</span>
          team<span v-if="employee.department"
            >, in the
            <span class="text-sb">{{ employee.department }}</span>
            department</span
          >
        </div>
        <div v-if="!employee.currentEngagement && employee.department">
          Currently part of the
          <span class="text-sb">{{ employee.department }}</span> department
        </div>
        <div v-if="employee.reportsTo">
          Reports to
          <span>
            <span class="text-sb">{{ employee.reportsTo }}</span>
            <span v-if="employee.managerEmail">
              <b-icon-dash></b-icon-dash>
              <a class="ml-1" :href="`mailto:${employee.managerEmail}`">
                <b-icon-envelope></b-icon-envelope>
                <span>
                  {{ employee.managerEmail }}
                </span>
              </a></span
            >
          </span>
        </div>
        <div v-if="employee.availableSince" class="text-success">
          Available since
          {{ employee.availableSince | moment('MMMM DD, YYYY') }}
        </div>
        <div v-else class="text-success">
          Available now
        </div>
      </b-col>
    </div>
    <template v-slot:modal-footer>
      <span
        v-if="!$store.getters.isEmployeeSelected(employee)"
        id="disabled-wrapper"
      >
        <b-button
          variant="outline-secondary"
          class="default-size"
          :disabled="!$store.getters.isTeamCreated"
          @click="add"
        >
          Add to Team
        </b-button>
        <b-tooltip
          v-if="!$store.getters.isTeamCreated"
          target="disabled-wrapper"
          >{{
            'Please create a team before adding members to it'
          }}</b-tooltip
        >
      </span>
      <b-button v-else variant="outline-secondary" @click="remove">
        Remove from Team
      </b-button>
      <b-button variant="primary" class="default-size" @click="close">
        Close
      </b-button>
    </template>
  </b-modal>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
// eslint-disable-next-line no-unused-vars
import { Employee } from '@/types/Employee';
import { getRandomColor } from '@/utils/helpers';

@Component({
  components: {},
  methods: { getRandomColor }
})
export default class EmployeeModal extends Vue {
  id = 'user-profile-modal';
  localeTimeZone: string = '';
  interval: any;
  loaded = false;

  get employee(): Employee {
    return this.$store.state.team.selectedEmployee;
  }

  mounted() {
    // this.initLocaleTimeZone();
  }

  async init() {
    this.loaded = false;
    try {
      await this.$store.dispatch('getCurrentEmployee');
    } finally {
      this.loaded = true;
    }
  }

  async add() {
    const response = await this.$store.dispatch(
      'addEmployeeToTeam',
      this.employee
    );
    if (response) this.$store.commit('UPDATE_EMPLOYEE_IN_TEAM', true);
    this.close();
  }

  async remove() {
    const response = await this.$store.dispatch('removeEmployeeFromTeam', {
      id: this.$store.getters.getTeamMemberIdForEmployeeId(this.employee.id),
      name: this.employee.name
    });
    if (response) this.$store.commit('UPDATE_EMPLOYEE_IN_TEAM', false);

    this.close();
  }

  close() {
    this.$bvModal.hide(this.id);
  }

  closed() {
    this.$router.push('/');
  }

  open() {
    this.$bvModal.show(this.id);
  }

  get initials() {
    let match = this.employee.name?.match(/\b\w/g) || [];
    let initials = ((match.shift() || '') + (match.pop() || '')).toUpperCase();
    return initials;
  }
  // Set timer for time to update in DOM
  initLocaleTimeZone() {
    // Set initial time
    this.updateLocaleTime();
    const seconds = 60 - new Date().getSeconds();
    // Set timeout to update time when clock is at xx:00
    setTimeout(() => {
      this.updateLocaleTime();
    }, seconds * 1000);
    // Set timeout to update clock after n seconds + 60s
    setTimeout(() => {
      this.updateLocaleTime();
      // Set interval for updating time every 60s after clock is at xx:00
      this.interval = setInterval(() => {
        this.updateLocaleTime();
      }, 60000);
    }, seconds * 1000 + 60000);
  }

  updateLocaleTime() {
    this.localeTimeZone = this.$store.getters.getLocaleTimeByUTC(
      this.employee.timeZone
    );
  }

  beforeDestroy() {
    clearInterval(this.interval);
  }
}
</script>

<style lang="scss">
#user-profile-modal {
  .modal-content {
    min-width: 600px;
    header {
      svg {
        z-index: 2;
        cursor: pointer;
        position: absolute;
        right: 24px;
      }
    }
    .modal-body {
      .linkedin {
        a {
          fill: #007bff;
          svg {
            width: 20px;
            height: 20px;
            margin-left: -3px;
            margin-top: -3px;
          }
          &:hover {
            svg {
              fill: #0056b3;
            }
          }
        }
      }
      .b-avatar {
        width: 7.5em !important;
        height: 7.5em !important;
      }
    }
  }
}
@media screen and (max-width: 1024px) {
  #user-profile-modal {
    .modal-content {
      min-width: 200px;
    }
  }
}
</style>
