<template>
  <div class="team-wrapper">
    <b-row
      v-for="(teamMember, index) in teamMembers"
      :key="`team_member_${index}`"
      no-gutters
      class="member-row"
    >
      <TeamMemberRow :teamMember="teamMember" />
    </b-row>
    <b-row v-if="!teamMembers.length" no-gutters class="mt-3 text-center">
      <b-col class="text-secondary"> No team members added..</b-col>
    </b-row>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import TeamMemberRow from '@/components/TeamMemberRow.vue';

@Component({
  components: { TeamMemberRow }
})
export default class Team extends Vue {
  mounted() {}

  get teamMembers() {
    return this.$store.state.team.teamMembers;
  }
}
</script>

<style lang="scss">
.team-wrapper {
  height: auto;
  min-height: 200px;
  overflow-y: auto;
}
@media screen and (max-width: 1024px) {
  .team-wrapper {
    min-height: auto;
  }
}
</style>
